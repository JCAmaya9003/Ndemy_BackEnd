package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.response.EnrollmentResponse;

import java.util.UUID;

public interface EnrollmentService {
    EnrollmentResponse enroll(UUID studentId, UUID courseId);
    EnrollmentResponse deactivateEnrollment(UUID studentId, UUID courseId);
    boolean hasUserPaidCourse(UUID userId, UUID courseId);
}
