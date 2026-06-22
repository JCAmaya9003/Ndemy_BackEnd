package org.example.ndemy_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.LessonProgressService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final LessonProgressService progressService;

    // Courses the student is enrolled in, with progress
    @GetMapping("/students/me/courses")
    public ResponseEntity<GeneralResponse> getStudentCourses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Courses retrieved successfully",
                HttpStatus.OK,
                progressService.getStudentCourses(currentUser.getId())
        );
    }

    // Progress for a specific course
    @GetMapping("/students/me/courses/{courseId}/progress")
    public ResponseEntity<GeneralResponse> getCourseProgress(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Progress retrieved successfully",
                HttpStatus.OK,
                progressService.getCourseProgress(currentUser.getId(), courseId)
        );
    }
}
