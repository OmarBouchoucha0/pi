package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.NotificationRequest;
import tn.esprit.pi.dto.NotificationResponse;
import tn.esprit.pi.enums.NotificationStatus;
import tn.esprit.pi.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // POST /notifications/send
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> send(
            @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.sendNotification(request));
    }

    // POST /notifications/send-multi?alertId=1&recipient=user@mail.com
    @PostMapping("/send-multi")
    public ResponseEntity<List<NotificationResponse>> sendMulti(
            @RequestParam Long alertId,
            @RequestParam String recipient) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.sendMultiChannel(alertId, recipient));
    }

    // GET /notifications
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // GET /notifications/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    // GET /notifications/alert/{alertId}
    @GetMapping("/alert/{alertId}")
    public ResponseEntity<List<NotificationResponse>> getByAlert(
            @PathVariable Long alertId) {
        return ResponseEntity.ok(notificationService.getByAlert(alertId));
    }

    // GET /notifications/recipient/{identifier}
    @GetMapping("/recipient/{identifier}")
    public ResponseEntity<List<NotificationResponse>> getByRecipient(
            @PathVariable String identifier) {
        return ResponseEntity.ok(notificationService.getByRecipient(identifier));
    }

    // GET /notifications/unread/{identifier}
    @GetMapping("/unread/{identifier}")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @PathVariable String identifier) {
        return ResponseEntity.ok(notificationService.getUnreadByRecipient(identifier));
    }

    // GET /notifications/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponse>> getByStatus(
            @PathVariable NotificationStatus status) {
        return ResponseEntity.ok(notificationService.getByStatus(status));
    }

    // PATCH /notifications/{id}/read
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // PATCH /notifications/{id}/retry
    @PatchMapping("/{id}/retry")
    public ResponseEntity<NotificationResponse> retry(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.retryNotification(id));
    }

    // POST /notifications/retry-all
    @PostMapping("/retry-all")
    public ResponseEntity<Void> retryAll() {
        notificationService.retryAllFailed();
        return ResponseEntity.ok().build();
    }

    // DELETE /notifications/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    // PUT /notifications/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> update(
            @PathVariable Long id,
            @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }
}
