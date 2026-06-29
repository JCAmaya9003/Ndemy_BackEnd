package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.services.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendCertificateEmail(String to, String studentName, String courseTitle, String certificateCode) {
        String subject = "¡Felicidades! Tu certificado de " + courseTitle;
        String body = "Hola " + studentName + ",\n\n"
                + "Has completado exitosamente el curso \"" + courseTitle + "\".\n"
                + "Tu codigo de certificado es: " + certificateCode + "\n\n"
                + "¡Felicidades!\n\n"
                + "El equipo de Ndemy";
        sendEmail(to, subject, body);
    }

    @Override
    public void sendCoursePublishedEmail(String to, String studentName, String courseTitle) {
        String subject = "Nuevo curso disponible: " + courseTitle;
        String body = "Hola " + studentName + ",\n\n"
                + "Un curso que tienes en tu wishlist ya está disponible: \"" + courseTitle + "\".\n"
                + "Ingresa a Ndemy para inscribirte.\n\n"
                + "El equipo de Ndemy";
        sendEmail(to, subject, body);
    }
}
