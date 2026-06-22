package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.CourseStatsResponse;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.repositories.CourseRepository;
import org.example.ndemy_backend.repositories.EnrollmentRepository;
import org.example.ndemy_backend.repositories.PaymentRepository;
import org.example.ndemy_backend.repositories.ReviewRepository;
import org.example.ndemy_backend.services.InstructorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;

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
}