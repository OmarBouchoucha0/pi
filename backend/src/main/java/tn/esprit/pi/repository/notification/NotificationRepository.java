package tn.esprit.pi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.pi.entity.Notification;
import tn.esprit.pi.enums.NotificationChannel;
import tn.esprit.pi.enums.NotificationStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAlertId(Long alertId);

    List<Notification> findByRecipientIdentifier(String recipientIdentifier);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByChannel(NotificationChannel channel);

    // Pour le retry : toutes les notifs FAILED avec retryCount < max
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetry);

    // Notifs non lues d'un destinataire
    List<Notification> findByRecipientIdentifierAndIsReadFalse(String recipientIdentifier);

    // Toutes les notifs d'une alerte par canal
    List<Notification> findByAlertIdAndChannel(Long alertId, NotificationChannel channel);
}
