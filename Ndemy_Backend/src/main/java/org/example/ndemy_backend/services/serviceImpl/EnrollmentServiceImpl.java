package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.EnrollmentResponse;
import org.example.ndemy_backend.exceptions.*;
import org.example.ndemy_backend.models.*;
import org.example.ndemy_backend.repositories.*;
import org.example.ndemy_backend.services.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public EnrollmentResponse enroll(UUID studentId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!course.getIsPublished()) {
            throw new CourseNotPublishedException("Course is not published yet");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyEnrolledException("Student is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment
                .builder()
                .course(course)
                .student(student)
                .isActive(true)
                .build();

        return toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse deactivateEnrollment(UUID studentId, UUID courseId) {
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Active enrollment not found"));

        enrollment.setIsActive(false);

        return toEnrollmentResponse(enrollmentRepository.save(enrollment));
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .isActive(enrollment.getIsActive())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .build();
    }

    @Override
    public boolean hasUserPaidCourse(UUID userId, UUID courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseIdAndIsActiveTrue(userId, courseId);
    }
}
