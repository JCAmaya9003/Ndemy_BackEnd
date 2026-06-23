package org.example.ndemy_backend.notifications.channels;

import org.example.ndemy_backend.notifications.NotificationChannel;
import org.example.ndemy_backend.notifications.NotificationChannelType;
import org.example.ndemy_backend.notifications.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationChannel.class);

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.SMS;
    }

    @Override
    public void send(NotificationMessage message) {
        // Stub: integración real con Twilio/etc. quedaría aquí.
        log.info("[SMS-STUB] Para: {} | Asunto: {}", message.getRecipient(), message.getSubject());
    }
}
