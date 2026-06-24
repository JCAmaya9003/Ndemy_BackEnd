package org.example.ndemy_backend.services;

public interface EmailService {

    void sendEmail(String to, String subject, String body);
}

