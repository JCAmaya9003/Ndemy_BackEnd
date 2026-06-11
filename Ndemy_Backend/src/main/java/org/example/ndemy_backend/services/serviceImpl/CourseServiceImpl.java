package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CourseRequest;
import org.example.ndemy_backend.dto.response.CourseDetailResponse;
import org.example.ndemy_backend.dto.response.CourseSummaryResponse;
import org.example.ndemy_backend.dto.response.LessonResponse;
import org.example.ndemy_backend.dto.response.ModuleResponse;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Lesson;
import org.example.ndemy_backend.models.Module;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.repositories.*;
import org.example.ndemy_backend.services.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    public CourseDetailResponse createCourse(CourseRequest request, UUID instructorId) {
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        Course course = Course
                .builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .durationHours(request.getDurationHours())
                .thumbnailUrl(request.getThumbnailUrl())
                .isPublished(false)
                .instructor(instructor)
                .build();

        return toCourseDetailResponse(courseRepository.save(course));
    }

    @Override
    public Page<CourseSummaryResponse> getCourses(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                           String search, Pageable pageable) {
        return courseRepository
                .findWithFilters(category, minPrice, maxPrice, search, pageable)
                .map(this::toCourseSummaryResponse);
    }

    @Override
    public CourseDetailResponse getCourseById(UUID courseId, UUID studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);

        return toCourseDetailResponse(course, isEnrolled);
    }

    @Override
    public CourseDetailResponse updateCourse(UUID courseId, CourseRequest request, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        verifyOwnership(course, instructorId);

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setCategory(request.getCategory());
        course.setDurationHours(request.getDurationHours());
        course.setThumbnailUrl(request.getThumbnailUrl());

        return toCourseDetailResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourseById(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User user = userRepository.findById(instructorId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // only the course instructor or an admin can delete this course
        if (!course.getInstructor().getId().equals(instructorId)
                && user.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not allowed to delete this course");
        }

        courseRepository.delete(course);
    }

    @Override
    public CourseDetailResponse publishCourse(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        verifyOwnership(course, instructorId);

        course.setIsPublished(true);

        return toCourseDetailResponse(courseRepository.save(course));
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("You are not allowed to modify this course");
        }
    }

    private LessonResponse toLessonResponse(Lesson lesson, boolean showContent) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .contentType(lesson.getContentType())
                .orderIndex(lesson.getOrderIndex())
                .contentUrl(showContent ? lesson.getContentUrl() : null)
                .build();
    }

    private ModuleResponse toModuleResponse(Module module, boolean showContent) {
        List<LessonResponse> lessons = lessonRepository
                .findByModuleIdOrderByOrderIndexAsc(module.getId())
                .stream()
                .map(l -> toLessonResponse(l, showContent))
                .toList();

        return ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .orderIndex(module.getOrderIndex())
                .lessons(lessons)
                .build();
    }

    private CourseDetailResponse toCourseDetailResponse(Course course, boolean isEnrolled) {

        List<ModuleResponse> modules = moduleRepository
                .findByCourseIdOrderByOrderIndexAsc(course.getId())
                .stream()
                .map(m -> toModuleResponse(m, isEnrolled))
                .toList();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .category(course.getCategory())
                .durationHours(course.getDurationHours())
                .thumbnailUrl(course.getThumbnailUrl())
                .isPublished(course.getIsPublished())
                .instructorName(course.getInstructor().getName())
                .createdAt(course.getCreatedAt())
                .modules(modules)
                .build();
    }

    private CourseDetailResponse toCourseDetailResponse(Course course) {

        List<ModuleResponse> modules = moduleRepository
                .findByCourseIdOrderByOrderIndexAsc(course.getId())
                .stream()
                .map(m -> toModuleResponse(m, true))
                .toList();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .category(course.getCategory())
                .durationHours(course.getDurationHours())
                .thumbnailUrl(course.getThumbnailUrl())
                .isPublished(course.getIsPublished())
                .instructorName(course.getInstructor().getName())
                .createdAt(course.getCreatedAt())
                .modules(modules)
                .build();
    }

    private CourseSummaryResponse toCourseSummaryResponse(Course course) {
        return CourseSummaryResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .price(course.getPrice())
                .category(course.getCategory())
                .instructorName(course.getInstructor().getName())
                .thumbnailUrl(course.getThumbnailUrl())
                .build();
    }
}
