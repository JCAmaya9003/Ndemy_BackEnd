package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.EnrollmentResponse;
import org.example.ndemy_backend.dto.response.ProgressResponse;
import org.example.ndemy_backend.dto.response.StudentCourseResponse;
import org.example.ndemy_backend.exceptions.*;
import org.example.ndemy_backend.models.*;
import org.example.ndemy_backend.repositories.*;
import org.example.ndemy_backend.services.EnrollmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CertificateRepository certificateRepository;
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

    @Override
    public ProgressResponse completeLesson(UUID studentId, UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        UUID courseId = lesson.getModule().getCourse().getId();

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(studentId, courseId)
                .orElseThrow(() -> new NotEnrolledException("Student is not enrolled in this course"));

        if (lessonProgressRepository.existsByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)) {
            throw new LessonAlreadyCompletedException("Lesson already completed");
        }

        LessonProgress progress = LessonProgress
                .builder()
                .enrollment(enrollment)
                .lesson(lesson)
                .build();

        lessonProgressRepository.save(progress);

        double progressPercent = calculateProgress(enrollment.getId(), courseId);
        boolean courseCompleted = progressPercent == 100.0;

        if (courseCompleted) {
            generateCertificate(studentId, courseId);
        }

        return ProgressResponse
                .builder()
                .lessonId(lessonId)
                .progress(progressPercent)
                .courseCompleted(courseCompleted)
                .build();
    }

    @Override
    public List<StudentCourseResponse> getStudentCourses(UUID studentId) {
        return enrollmentRepository.findByStudentIdAndIsActiveTrue(studentId)
                .stream()
                .map(e -> toStudentCourseResponse(e, studentId))
                .toList();
    }

    @Override
    public StudentCourseResponse getCourseProgress(UUID studentId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(studentId, courseId)
                .orElseThrow(() -> new NotEnrolledException("Student is not enrolled in this course"));

        return toStudentCourseResponse(enrollment, studentId);
    }

    private void generateCertificate(UUID studentId, UUID courseId) {
        if (certificateRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new CertificateAlreadyExistsException("Certificate already exists");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Certificate certificate = Certificate.builder()
                .course(course)
                .certificateCode(UUID.randomUUID().toString())
                .student(student)
                .build();

        certificateRepository.save(certificate);
    }

    private double calculateProgress(UUID enrollmentId, UUID courseId) {
        int completed = lessonProgressRepository.countByEnrollmentId(enrollmentId);
        int total = lessonRepository.countByModuleCourseId(courseId);
        if (total == 0) return 0.0;
        return (completed * 100.0) / total;
    }

    private StudentCourseResponse toStudentCourseResponse(Enrollment enrollment, UUID studentId) {
        UUID courseId = enrollment.getCourse().getId();
        double progress = calculateProgress(enrollment.getId(), courseId);

        return StudentCourseResponse.builder()
                .courseId(courseId)
                .courseTitle(enrollment.getCourse().getTitle())
                .thumbnailUrl(enrollment.getCourse().getThumbnailUrl())
                .progress(progress)
                .isCompleted(progress == 100.0)
                .build();
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
}
