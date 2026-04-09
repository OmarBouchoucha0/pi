package tn.esprit.pi.service;

import java.util.List;

import tn.esprit.pi.dto.NotificationRequest;
import tn.esprit.pi.dto.NotificationResponse;
import tn.esprit.pi.enums.NotificationStatus;

public interface NotificationService {

    // Envoi simple
    NotificationResponse sendNotification(NotificationRequest request);

    // Envoi multi-canal (IN_APP + EMAIL + SMS en une fois)
    List<NotificationResponse> sendMultiChannel(Long alertId, String recipientIdentifier);

    // Read
    NotificationResponse getById(Long id);
    List<NotificationResponse> getAll();
    List<NotificationResponse> getByAlert(Long alertId);
    List<NotificationResponse> getByRecipient(String recipientIdentifier);
    List<NotificationResponse> getByStatus(NotificationStatus status);
    List<NotificationResponse> getUnreadByRecipient(String recipientIdentifier);

    // Actions
    NotificationResponse markAsRead(Long id);
    NotificationResponse retryNotification(Long id);
    void retryAllFailed();

    // Delete
    void deleteNotification(Long id);

    NotificationResponse updateNotification(Long id, NotificationRequest request);
}
