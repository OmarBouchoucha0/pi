package tn.esprit.pi.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.pi.dto.NotificationRequest;
import tn.esprit.pi.dto.NotificationResponse;
import tn.esprit.pi.entity.Alert;
import tn.esprit.pi.entity.Notification;
import tn.esprit.pi.enums.NotificationChannel;
import tn.esprit.pi.enums.NotificationStatus;
import tn.esprit.pi.repository.AlertRepository;
import tn.esprit.pi.repository.NotificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AlertRepository alertRepository;

    private static final int MAX_RETRY = 3;

    // ─── Mapping ─────────────────────────────────────────────────

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .alertId(n.getAlert().getId())
                .alertTitle(n.getAlert().getTitle())
                .recipientIdentifier(n.getRecipientIdentifier())
                .channel(n.getChannel())
                .status(n.getStatus())
                .retryCount(n.getRetryCount())
                .isRead(n.getIsRead())
                .sentAt(n.getSentAt())
                .build();
    }

    // ─── Dispatch selon le canal ──────────────────────────────────
    // Simule l'envoi réel — à remplacer par un vrai provider
    // (JavaMailSender pour EMAIL, Twilio pour SMS, WebSocket pour IN_APP)

    private NotificationStatus dispatch(Notification notification) {
        try {
            switch (notification.getChannel()) {
                case EMAIL -> {
                    log.info("[EMAIL] Envoi à {} — alerte: {}",
                            notification.getRecipientIdentifier(),
                            notification.getAlert().getTitle());
                    // TODO: mailSender.send(...)
                }
                case SMS -> {
                    log.info("[SMS] Envoi à {} — alerte: {}",
                            notification.getRecipientIdentifier(),
                            notification.getAlert().getTitle());
                    // TODO: twilioClient.send(...)
                }
                case IN_APP -> {
                    log.info("[IN_APP] Push WebSocket à {} — alerte: {}",
                            notification.getRecipientIdentifier(),
                            notification.getAlert().getTitle());
                    // TODO: messagingTemplate.convertAndSendToUser(...)
                }
            }
            return NotificationStatus.SENT;
        } catch (Exception e) {
            log.error("[DISPATCH ERROR] Canal: {} | Erreur: {}",
                    notification.getChannel(), e.getMessage());
            return NotificationStatus.FAILED;
        }
    }

    // ─── Envoi simple ─────────────────────────────────────────────

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        Alert alert = alertRepository.findById(request.getAlertId())
                .orElseThrow(() -> new RuntimeException("Alert not found: " + request.getAlertId()));

        Notification notification = Notification.builder()
                .alert(alert)
                .recipientIdentifier(request.getRecipientIdentifier())
                .channel(request.getChannel())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .isRead(false)
                .build();

        // Dispatch immédiat
        NotificationStatus result = dispatch(notification);
        notification.setStatus(result);

        return toResponse(notificationRepository.save(notification));
    }

    // ─── Envoi multi-canal ────────────────────────────────────────
    // Crée une notification par canal (IN_APP + EMAIL + SMS) en une seule requête

    @Override
    public List<NotificationResponse> sendMultiChannel(Long alertId, String recipientIdentifier) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));

        List<Notification> notifications = Arrays.stream(NotificationChannel.values())
                .map(channel -> {
                    Notification n = Notification.builder()
                            .alert(alert)
                            .recipientIdentifier(recipientIdentifier)
                            .channel(channel)
                            .status(NotificationStatus.PENDING)
                            .retryCount(0)
                            .isRead(false)
                            .build();
                    n.setStatus(dispatch(n));
                    return n;
                })
                .collect(Collectors.toList());

        return notificationRepository.saveAll(notifications)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Read ─────────────────────────────────────────────────────

    @Override
    public NotificationResponse getById(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        return toResponse(n);
    }

    @Override
    public List<NotificationResponse> getAll() {
        return notificationRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getByAlert(Long alertId) {
        return notificationRepository.findByAlertId(alertId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getByRecipient(String recipientIdentifier) {
        return notificationRepository.findByRecipientIdentifier(recipientIdentifier)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadByRecipient(String recipientIdentifier) {
        return notificationRepository.findByRecipientIdentifierAndIsReadFalse(recipientIdentifier)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Mark as read ─────────────────────────────────────────────

    @Override
    public NotificationResponse markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        n.setIsRead(true);
        return toResponse(notificationRepository.save(n));
    }

    // ─── Retry logique ────────────────────────────────────────────
    // Réessaie une notification FAILED si retryCount < MAX_RETRY (3)

    @Override
    public NotificationResponse retryNotification(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));

        if (n.getStatus() != NotificationStatus.FAILED) {
            throw new RuntimeException("Seules les notifications FAILED peuvent être relancées");
        }
        if (n.getRetryCount() >= MAX_RETRY) {
            throw new RuntimeException("Limite de retry atteinte (" + MAX_RETRY + ")");
        }

        n.setRetryCount(n.getRetryCount() + 1);
        NotificationStatus result = dispatch(n);
        n.setStatus(result);

        log.info("[RETRY] Notification {} — tentative {}/{} — résultat: {}",
                id, n.getRetryCount(), MAX_RETRY, result);

        return toResponse(notificationRepository.save(n));
    }

    // ─── Retry automatique de toutes les FAILED ───────────────────
    // Appelé manuellement ou par un @Scheduled

    @Override
    public void retryAllFailed() {
        List<Notification> failed = notificationRepository
                .findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, MAX_RETRY);

        log.info("[RETRY ALL] {} notifications FAILED trouvées", failed.size());

        failed.forEach(n -> {
            n.setRetryCount(n.getRetryCount() + 1);
            n.setStatus(dispatch(n));
        });

        notificationRepository.saveAll(failed);
    }

    // ─── Delete ───────────────────────────────────────────────────

    @Override
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found: " + id);
        }
        notificationRepository.deleteById(id);
    }
    @Override
    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));

        Alert alert = alertRepository.findById(request.getAlertId())
                .orElseThrow(() -> new RuntimeException("Alert not found: " + request.getAlertId()));

        notification.setAlert(alert);
        notification.setRecipientIdentifier(request.getRecipientIdentifier());
        notification.setChannel(request.getChannel());

        return toResponse(notificationRepository.save(notification));
    }
}
