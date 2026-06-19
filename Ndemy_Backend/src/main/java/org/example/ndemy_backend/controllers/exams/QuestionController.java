package org.example.ndemy_backend.controllers.exams;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.exams.QuestionRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.exams.QuestionService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // orderIndex is optional, same shifting logic as modules and lessons

    // Questions can only be created by the instructor owner of the parent course
    @PostMapping("/exams/{examId}/questions")
    public ResponseEntity<GeneralResponse> createQuestion(
            @PathVariable UUID examId,
            @RequestBody @Valid QuestionRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Question created successfully",
                HttpStatus.CREATED,
                questionService.createQuestion(examId, request, currentUser.getId())
        );
    }

    // Questions can only be edited by the owner instructor
    @PutMapping("/questions/{id}")
    public ResponseEntity<GeneralResponse> updateQuestion(
            @PathVariable UUID id,
            @RequestBody @Valid QuestionRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Question updated successfully",
                HttpStatus.OK,
                questionService.updateQuestion(id, request, currentUser.getId())
        );
    }

    // Questions can only be deleted by the owner instructor
    @DeleteMapping("/questions/{id}")
    public ResponseEntity<GeneralResponse> deleteQuestion(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        questionService.deleteQuestion(id, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Question deleted successfully",
                HttpStatus.OK,
                null
        );
    }
}