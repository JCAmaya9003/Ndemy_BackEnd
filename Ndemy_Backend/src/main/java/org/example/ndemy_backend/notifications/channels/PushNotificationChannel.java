package org.example.ndemy_backend.notifications.channels;

import org.example.ndemy_backend.notifications.NotificationChannel;
import org.example.ndemy_backend.notifications.NotificationChannelType;
import org.example.ndemy_backend.notifications.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationChannel.class);

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.PUSH;
    }

    @Override
    public void send(NotificationMessage message) {
        // Stub: integración real con FCM/APNs quedaría aquí.
        log.info("[PUSH-STUB] Para: {} | Asunto: {}", message.getRecipient(), message.getSubject());
    }
}
