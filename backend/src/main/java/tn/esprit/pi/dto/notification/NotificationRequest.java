package tn.esprit.pi.dto;

import lombok.Data;
import tn.esprit.pi.enums.NotificationChannel;

@Data
public class NotificationRequest {
    private Long alertId;
    private String recipientIdentifier;
    private NotificationChannel channel;
}
