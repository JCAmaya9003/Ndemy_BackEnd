package org.example.ndemy_backend.notifications;

public interface NotificationChannel {

    NotificationChannelType getType();

    void send(NotificationMessage message);
}
