package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.LessonRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.LessonService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    // orderIndex is optional in lesson requests
    // if not provided, it will be assigned automatically at the end
    // if the index already exists, existing lessons will be shifted to the right
    // this allows inserting lessons between existing ones

    // Lessons can only be created by instructors in modules that belong to their own courses
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<GeneralResponse> createLesson(
            @PathVariable UUID moduleId,
            @RequestBody @Valid LessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Lesson created successfully",
                HttpStatus.CREATED,
                lessonService.createLesson(moduleId, request, currentUser.getId())
        );
    }

    // Lessons can only be edited by the owner instructor
    @PutMapping("/lessons/{id}")
    public ResponseEntity<GeneralResponse> updateLesson(
            @PathVariable UUID id,
            @RequestBody @Valid LessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Lesson updated successfully",
                HttpStatus.OK,
                lessonService.updateLesson(id, request, currentUser.getId())
        );
    }

    // Lessons can only be deleted by the owner instructor
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<GeneralResponse> deleteLesson(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        lessonService.deleteLesson(id, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Lesson deleted successfully",
                HttpStatus.OK,
                null
        );
    }
}
