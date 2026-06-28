package org.example.ndemy_backend.services;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendCertificateEmail(String to, String studentName, String courseTitle, String certificateCode);

    void sendCoursePublishedEmail(String to, String studentName, String courseTitle);
}

