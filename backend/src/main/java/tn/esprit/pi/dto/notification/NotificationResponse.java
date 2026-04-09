package tn.esprit.pi.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import tn.esprit.pi.enums.NotificationChannel;
import tn.esprit.pi.enums.NotificationStatus;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private Long alertId;
    private String alertTitle;
    private String recipientIdentifier;
    private NotificationChannel channel;
    private NotificationStatus status;
    private Integer retryCount;
    private Boolean isRead;
    private LocalDateTime sentAt;
}
