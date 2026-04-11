package tn.esprit.pi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import tn.esprit.pi.dto.NotificationRequest;
import tn.esprit.pi.dto.NotificationResponse;
import tn.esprit.pi.enums.NotificationStatus;
import tn.esprit.pi.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for sending and managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // POST /notifications/send
    @PostMapping("/send")
    @Operation(summary = "Send notification", description = "Sends a notification to a recipient")
    @ApiResponse(responseCode = "201", description = "Notification sent successfully")
    public ResponseEntity<NotificationResponse> send(
            @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.sendNotification(request));
    }

    // POST /notifications/send-multi
    @PostMapping("/send-multi")
    @Operation(summary = "Send multi-channel notification", description = "Sends a notification via multiple channels")
    @ApiResponse(responseCode = "201", description = "Notification sent successfully")
    public ResponseEntity<List<NotificationResponse>> sendMulti(
            @RequestParam Long alertId,
            @RequestParam String recipient) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.sendMultiChannel(alertId, recipient));
    }

    // GET /notifications
    @GetMapping
    @Operation(summary = "Get all notifications", description = "Retrieves all notifications")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // GET /notifications/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a specific notification by its ID")
    @ApiResponse(responseCode = "200", description = "Notification found")
    @ApiResponse(responseCode = "404", description = "Notification not found")
    public ResponseEntity<NotificationResponse> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    // GET /notifications/alert/{alertId}
    @GetMapping("/alert/{alertId}")
    @Operation(summary = "Get notifications by alert", description = "Retrieves all notifications for a specific alert")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationResponse>> getByAlert(
            @PathVariable Long alertId) {
        return ResponseEntity.ok(notificationService.getByAlert(alertId));
    }

    // GET /notifications/recipient/{identifier}
    @GetMapping("/recipient/{identifier}")
    @Operation(summary = "Get notifications by recipient", description = "Retrieves all notifications for a specific recipient")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationResponse>> getByRecipient(
            @PathVariable String identifier) {
        return ResponseEntity.ok(notificationService.getByRecipient(identifier));
    }

    // GET /notifications/unread/{identifier}
    @GetMapping("/unread/{identifier}")
    @Operation(summary = "Get unread notifications", description = "Retrieves all unread notifications for a recipient")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationResponse>> getUnread(
            @PathVariable String identifier) {
        return ResponseEntity.ok(notificationService.getUnreadByRecipient(identifier));
    }

    // GET /notifications/status/{status}
    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status", description = "Retrieves all notifications with a specific status")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationResponse>> getByStatus(
            @PathVariable NotificationStatus status) {
        return ResponseEntity.ok(notificationService.getByStatus(status));
    }

    // PATCH /notifications/{id}/read
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a notification as read")
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    // PATCH /notifications/{id}/retry
    @PatchMapping("/{id}/retry")
    @Operation(summary = "Retry notification", description = "Retries sending a failed notification")
    @ApiResponse(responseCode = "200", description = "Notification retry initiated")
    public ResponseEntity<NotificationResponse> retry(
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.retryNotification(id));
    }

    // POST /notifications/retry-all
    @PostMapping("/retry-all")
    @Operation(summary = "Retry all failed", description = "Retries all failed notifications")
    @ApiResponse(responseCode = "200", description = "Retry initiated")
    public ResponseEntity<Void> retryAll() {
        notificationService.retryAllFailed();
        return ResponseEntity.ok().build();
    }

    // DELETE /notifications/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Deletes a notification")
    @ApiResponse(responseCode = "204", description = "Notification deleted successfully")
    @ApiResponse(responseCode = "404", description = "Notification not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    // PUT /notifications/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Update notification", description = "Updates an existing notification")
    @ApiResponse(responseCode = "200", description = "Notification updated successfully")
    @ApiResponse(responseCode = "404", description = "Notification not found")
    public ResponseEntity<NotificationResponse> update(
            @PathVariable Long id,
            @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }
}
