package org.example.ndemy_backend.controllers.exams;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.exams.ExamRequest;
import org.example.ndemy_backend.dto.request.exams.ExamSubmitRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.services.exams.ExamService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class ExamController {

    private final ExamService examService;

    // Exams can only be created by instructors for their own courses
    @PostMapping("/{courseId}/exam")
    public ResponseEntity<GeneralResponse> createExam(
            @PathVariable UUID courseId,
            @RequestBody @Valid ExamRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Exam created successfully",
                HttpStatus.CREATED,
                examService.createExam(courseId, request, currentUser.getId())
        );
    }

    // Public for enrolled students with 100% progress, full access for instructor owner and ADMIN
    // Owner instructor and ADMIN see correct answers, students don't
    @GetMapping("/{courseId}/exam")
    public ResponseEntity<GeneralResponse> getExam(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        boolean isPrivileged = currentUser.getRole() == Role.INSTRUCTOR
                || currentUser.getRole() == Role.ADMIN;

        return ResponseBuilder.buildResponse(
                "Exam retrieved successfully",
                HttpStatus.OK,
                examService.getExam(courseId, currentUser.getId(), isPrivileged)
        );
    }

    // Exams can only be edited by the owner instructor
    @PutMapping("/{courseId}/exam")
    public ResponseEntity<GeneralResponse> updateExam(
            @PathVariable UUID courseId,
            @RequestBody @Valid ExamRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Exam updated successfully",
                HttpStatus.OK,
                examService.updateExam(courseId, request, currentUser.getId())
        );
    }

    // Exams can only be deleted by the owner instructor or ADMIN
    @DeleteMapping("/{courseId}/exam")
    public ResponseEntity<GeneralResponse> deleteExam(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        examService.deleteExam(courseId, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Exam deleted successfully",
                HttpStatus.OK,
                null
        );
    }

    // Students submit their answers — requires active enrollment and 100% lesson progress
    // Max 3 attempts, on the 3rd failure the course progress resets
    @PostMapping("/{courseId}/exam/submit")
    public ResponseEntity<GeneralResponse> submitExam(
            @PathVariable UUID courseId,
            @RequestBody @Valid ExamSubmitRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Exam submitted successfully",
                HttpStatus.OK,
                examService.submitExam(courseId, currentUser.getId(), request)
        );
    }
}