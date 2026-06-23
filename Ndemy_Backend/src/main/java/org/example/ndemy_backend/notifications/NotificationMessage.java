package org.example.ndemy_backend.notifications;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationMessage {
    private String recipient;
    private String subject;
    private String body;
}
