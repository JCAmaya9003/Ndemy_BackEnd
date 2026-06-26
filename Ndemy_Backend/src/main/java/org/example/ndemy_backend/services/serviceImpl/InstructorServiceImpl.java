package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.CourseStatsResponse;
import org.example.ndemy_backend.dto.response.StudentProgressResponse;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Enrollment;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.exams.Exam;
import org.example.ndemy_backend.models.exams.ExamAttempt;
import org.example.ndemy_backend.repositories.CourseRepository;
import org.example.ndemy_backend.repositories.EnrollmentRepository;
import org.example.ndemy_backend.repositories.PaymentRepository;
import org.example.ndemy_backend.repositories.ReviewRepository;
import org.example.ndemy_backend.repositories.exams.ExamAttemptRepository;
import org.example.ndemy_backend.repositories.exams.ExamRepository;
import org.example.ndemy_backend.services.InstructorService;
import org.example.ndemy_backend.services.LessonProgressService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;

    private final LessonProgressService lessonProgressService;

    @Override
    public List<CourseStatsResponse> getInstructorCourses(UUID instructorId) {
        List<Course> courses = courseRepository.findByInstructorId(instructorId);

        Map<UUID, BigDecimal> revenueByCourse = paymentRepository
                .findCourseRevenueByInstructor(instructorId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (BigDecimal) row[2]
                ));

        return courses.stream()
                .map(course -> toStatsResponse(course, revenueByCourse))
                .toList();
    }

    @Override
    public List<StudentProgressResponse> getCourseStudents(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        verifyOwnership(course, instructorId);

        // exam might not exist defensively, even though publish validation requires it
        Optional<Exam> examOpt = examRepository.findByCourseId(courseId);

        List<Enrollment> enrollments = enrollmentRepository
                .findByCourseIdAndIsActiveTrue(courseId);

        return enrollments.stream()
                .map(e -> toStudentProgressResponse(e, courseId, examOpt))
                .toList();
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("You are not allowed to view this course's students");
        }
    }

    private CourseStatsResponse toStatsResponse(Course course, Map<UUID, BigDecimal> revenueByCourse) {
        UUID courseId = course.getId();

        int enrolledCount = enrollmentRepository.countByCourseIdAndIsActiveTrue(courseId);
        Double averageRating = reviewRepository.findAverageRatingByCourseId(courseId).orElse(0.0);
        BigDecimal totalRevenue = revenueByCourse.getOrDefault(courseId, BigDecimal.ZERO);

        return CourseStatsResponse.builder()
                .courseId(courseId)
                .title(course.getTitle())
                .category(course.getCategory())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .isPublished(course.getIsPublished())
                .enrolledCount(enrolledCount)
                .averageRating(averageRating)
                .totalRevenue(totalRevenue)
                .build();
    }

    private StudentProgressResponse toStudentProgressResponse(
            Enrollment enrollment, UUID courseId, Optional<Exam> examOpt) {

        double progress = lessonProgressService.calculateProgress(enrollment.getId(), courseId);
        User student = enrollment.getStudent();

        Boolean examPassed = null;
        Integer examAttemptsUsed = null;
        BigDecimal bestScore = BigDecimal.ZERO;

        if (examOpt.isPresent()) {
            Optional<ExamAttempt> attempt = examAttemptRepository
                    .findByStudentIdAndExamId(student.getId(), examOpt.get().getId());

            examPassed = attempt.map(ExamAttempt::getPassed).orElse(false);
            examAttemptsUsed = attempt.map(ExamAttempt::getAttempts).orElse(0);
            bestScore = attempt.map(ExamAttempt::getBestScore).orElse(null);
        }

        return StudentProgressResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .studentEmail(student.getEmail())
                .progress(progress)
                .isCompleted(progress == 100.0)
                .enrolledAt(enrollment.getEnrolledAt())
                .examPassed(examPassed)
                .examAttemptsUsed(examAttemptsUsed)
                .bestScore(bestScore)
                .build();
    }
}