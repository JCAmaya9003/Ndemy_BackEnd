package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CourseRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.CourseService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // Courses can only be created by instructors, check role
    @PostMapping
    public ResponseEntity<GeneralResponse> createCourse(
            @RequestBody @Valid CourseRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Course created successfully",
                HttpStatus.CREATED,
                courseService.createCourse(request, currentUser.getId())
        );
    }

    // Public, paginated and with filters
    // Pageable has three params: page, size and sort
    // example: /courses?page=0&size=10&sort=price,asc
    // sort can be used more than once and the order you write it, defines priority when its ordered
    // default for pageable is page = 0, size = 20, sort = null
    @GetMapping
    public ResponseEntity<GeneralResponse> getCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseBuilder.buildResponse(
                "Courses found successfully",
                HttpStatus.OK,
                courseService.getCourses(category, minPrice, maxPrice, search, pageable)
        );
    }

    // Public, if you are a user enrolled in a course, you can see the content url in lessons detail
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getCourseById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID userId = currentUser != null ? currentUser.getId() : null;

        return ResponseBuilder.buildResponse(
                "Course found successfully",
                HttpStatus.OK,
                courseService.getCourseById(id, userId)
        );
    }

    // Courses can only be edited by the owner instructor
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateCourse(
            @PathVariable UUID id,
            @RequestBody @Valid CourseRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Course updated successfully",
                HttpStatus.OK,
                courseService.updateCourse(id, request, currentUser.getId())
        );
    }

    // Courses can only be deleted by the owner instructor and admins
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        courseService.deleteCourseById(id, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Course deleted succesfully",
                HttpStatus.OK,
                null
        );
    }

    // Courses can only be published by the owner instructor
    @PatchMapping("/{id}/publish")
    public ResponseEntity<GeneralResponse> publishCourse(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Course published successfully",
                HttpStatus.OK,
                courseService.publishCourse(id, currentUser.getId())
        );
    }
}
