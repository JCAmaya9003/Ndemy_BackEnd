package org.example.ndemy_backend.notifications.channels;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.notifications.NotificationChannel;
import org.example.ndemy_backend.notifications.NotificationChannelType;
import org.example.ndemy_backend.notifications.NotificationMessage;
import org.example.ndemy_backend.services.EmailService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

    private final EmailService emailService;

    @Override
    public NotificationChannelType getType() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public void send(NotificationMessage message) {
        emailService.sendEmail(message.getRecipient(), message.getSubject(), message.getBody());
    }
}