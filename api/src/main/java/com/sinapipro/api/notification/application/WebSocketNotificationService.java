package com.sinapipro.api.notification.application;

import com.sinapipro.api.notification.domain.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Pushes notifications to connected WebSocket clients via STOMP.
 */
@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcast(Notification notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        if (notification.getRecipient() != null) {
            messagingTemplate.convertAndSendToUser(
                    notification.getRecipient(), "/queue/alerts", notification);
        }
    }
}
