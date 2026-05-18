package com.sinapipro.api.notification.api;

import com.sinapipro.api.notification.application.NotificationService;
import com.sinapipro.api.notification.domain.Notification;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Notifications", description = "Cross-module alerts and notification management")
@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) { this.notificationService = notificationService; }

    @Operation(summary = "Generate alerts for a budget (scans RFIs, equipment, contracts)")
    @PostMapping("/projects/{projectId}/notifications/generate")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    List<NotificationResponse> generate(@PathVariable UUID projectId) {
        return notificationService.generateAlerts(projectId).stream().map(NotificationResponse::from).toList();
    }

    @Operation(summary = "Get unread notifications for a user")
    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<NotificationResponse> unread(Principal principal, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(notificationService.getUnread(principal.getName(), pageable).map(NotificationResponse::from));
    }

    @Operation(summary = "Mark notification as read")
    @PostMapping("/notifications/{id}/read")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void markRead(@PathVariable UUID id) {
        notificationService.markRead(id);
    }

    @Operation(summary = "Count unread notifications")
    @GetMapping("/notifications/count")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    UnreadCount countUnread(Principal principal) {
        return new UnreadCount(notificationService.countUnread(principal.getName()));
    }

    record NotificationResponse(UUID id, String type, String severity, String title, String message,
                                String entityType, UUID entityId, Boolean read, Instant createdAt) {
        static NotificationResponse from(Notification n) {
            return new NotificationResponse(n.getId(), n.getType(), n.getSeverity(), n.getTitle(),
                    n.getMessage(), n.getEntityType(), n.getEntityId(), n.getRead(), n.getCreatedAt());
        }
    }
    record UnreadCount(long count) {}
}
