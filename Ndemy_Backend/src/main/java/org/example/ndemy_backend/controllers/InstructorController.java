package org.example.ndemy_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.InstructorService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/instructor")
public class InstructorController {

    private final InstructorService instructorService;

    @GetMapping("/me/courses")
    public ResponseEntity<GeneralResponse> getInstructorCourses(
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Courses retrieved successfully",
                HttpStatus.OK,
                instructorService.getInstructorCourses(currentUser.getId())
        );
    }

    @GetMapping("/me/courses/{id}/students")
    public ResponseEntity<GeneralResponse> getCourseStudents(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Students retrieved successfully",
                HttpStatus.OK,
                instructorService.getCourseStudents(id, currentUser.getId())
        );
    }
}
