package org.example.ndemy_backend.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final Map<NotificationChannelType, NotificationChannel> channels;

    public NotificationDispatcher(List<NotificationChannel> channelList) {
        this.channels = channelList.stream()
                .collect(Collectors.toMap(NotificationChannel::getType, Function.identity()));
    }

    public void dispatch(NotificationChannelType type, NotificationMessage message) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) {
            log.warn("No hay canal registrado para el tipo {}", type);
            return;
        }
        try {
            channel.send(message);
        } catch (Exception e) {
            // Una notificación fallida NUNCA debe romper la operación de negocio.
            log.error("Fallo al enviar notificación por {}: {}", type, e.getMessage());
        }
    }
}
