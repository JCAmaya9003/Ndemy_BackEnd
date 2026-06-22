package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.ProgressResponse;
import org.example.ndemy_backend.dto.response.StudentCourseResponse;
import org.example.ndemy_backend.exceptions.LessonAlreadyCompletedException;
import org.example.ndemy_backend.exceptions.NotEnrolledException;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.models.Enrollment;
import org.example.ndemy_backend.models.Lesson;
import org.example.ndemy_backend.models.LessonProgress;
import org.example.ndemy_backend.repositories.*;
import org.example.ndemy_backend.services.LessonProgressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonProgressServiceImpl implements LessonProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public ProgressResponse completeLesson(UUID studentId, UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        if (userRepository.findById(studentId).isEmpty()) {
            throw new ResourceNotFoundException("Student not found");
        }

        UUID courseId = lesson.getModule().getCourse().getId();

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(studentId, courseId)
                .orElseThrow(() -> new NotEnrolledException("Student is not enrolled in this course"));

        if (lessonProgressRepository.existsByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)) {
            throw new LessonAlreadyCompletedException("Lesson already completed");
        }

        LessonProgress progress = LessonProgress.builder()
                .enrollment(enrollment)
                .lesson(lesson)
                .build();

        lessonProgressRepository.save(progress);

        double progressPercent = calculateProgress(enrollment.getId(), courseId);
        boolean courseCompleted = progressPercent == 100.0;

        return ProgressResponse.builder()
                .lessonId(lessonId)
                .progress(progressPercent)
                .courseCompleted(courseCompleted)
                .build();
    }

    @Override
    public List<StudentCourseResponse> getStudentCourses(UUID studentId) {
        return enrollmentRepository.findByStudentIdAndIsActiveTrue(studentId)
                .stream()
                .map(this::toStudentCourseResponse)
                .toList();
    }

    @Override
    public StudentCourseResponse getCourseProgress(UUID studentId, UUID courseId) {
        if (courseRepository.findById(courseId).isEmpty()) {
            throw new ResourceNotFoundException("Course not found");
        }

        if (userRepository.findById(studentId).isEmpty()) {
            throw new ResourceNotFoundException("Student not found");
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(studentId, courseId)
                .orElseThrow(() -> new NotEnrolledException("Student is not enrolled in this course"));

        return toStudentCourseResponse(enrollment);
    }

    @Override
    public double calculateProgress(UUID enrollmentId, UUID courseId) {
        int completed = lessonProgressRepository.countByEnrollmentId(enrollmentId);
        int total = lessonRepository.countByModuleCourseId(courseId);
        if (total == 0) return 0.0;
        return (completed * 100.0) / total;
    }

    private StudentCourseResponse toStudentCourseResponse(Enrollment enrollment) {
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
}