package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.response.EnrollmentResponse;
import org.example.ndemy_backend.dto.response.ProgressResponse;
import org.example.ndemy_backend.dto.response.StudentCourseResponse;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    EnrollmentResponse enroll(UUID studentId, UUID courseId);
    EnrollmentResponse deactivateEnrollment(UUID studentId, UUID courseId);
    ProgressResponse completeLesson(UUID studentId, UUID lessonId);
    List<StudentCourseResponse> getStudentCourses(UUID studentId);
    StudentCourseResponse getCourseProgress(UUID studentId, UUID courseId);
}
