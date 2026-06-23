package org.example.ndemy_backend.notifications;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDispatcher dispatcher;

    public void notifyCertificateIssued(String email, String studentName,
                                        String courseTitle, String certificateCode) {
        String subject = "¡Felicidades! Tu certificado de " + courseTitle;
        String body = "Hola " + studentName + ",\n\n"
                + "Has completado exitosamente el curso \"" + courseTitle + "\".\n"
                + "Tu código de certificado es: " + certificateCode + "\n\n"
                + "¡Felicidades!\n\nEl equipo de Ndemy";

        dispatcher.dispatch(NotificationChannelType.EMAIL,
                NotificationMessage.builder()
                        .recipient(email).subject(subject).body(body).build());
    }

    public void notifyCoursePublished(String email, String studentName, String courseTitle) {
        String subject = "Nuevo curso disponible: " + courseTitle;
        String body = "Hola " + studentName + ",\n\n"
                + "Un curso que tienes en tu wishlist ya está disponible: \"" + courseTitle + "\".\n"
                + "Ingresa a Ndemy para inscribirte.\n\nEl equipo de Ndemy";

        dispatcher.dispatch(NotificationChannelType.EMAIL,
                NotificationMessage.builder()
                        .recipient(email).subject(subject).body(body).build());
    }
}
