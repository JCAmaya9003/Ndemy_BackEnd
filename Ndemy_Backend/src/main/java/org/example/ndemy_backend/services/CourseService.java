package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.CourseRequest;
import org.example.ndemy_backend.dto.response.CourseDetailResponse;
import org.example.ndemy_backend.dto.response.CourseSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseDetailResponse createCourse(CourseRequest request, UUID instructorId);
    Page<CourseSummaryResponse> getCourses(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                           String search, Pageable pageable);
    // studentId is included to decide if it needs to show contentUrl
    CourseDetailResponse getCourseById(UUID courseId, UUID studentId);
    CourseDetailResponse updateCourse(UUID courseId, CourseRequest request, UUID instructorId);
    void deleteCourseById(UUID courseId, UUID instructorId);
    CourseDetailResponse publishCourse(UUID courseId, UUID instructorId);
    List<CourseSummaryResponse> getInstructorCourses(UUID instructorId);
}
