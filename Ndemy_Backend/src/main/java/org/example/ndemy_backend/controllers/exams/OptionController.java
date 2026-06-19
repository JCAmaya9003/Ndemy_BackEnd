package org.example.ndemy_backend.controllers.exams;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.exams.OptionRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.exams.OptionService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;

    // Only one option per question can be marked as correct
    // marking a new one as correct automatically unsets the previous one

    // Order of options is shuffled to prevent copying

    // Options can only be created by the instructor owner of the parent course
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<GeneralResponse> createOption(
            @PathVariable UUID questionId,
            @RequestBody @Valid OptionRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Option created successfully",
                HttpStatus.CREATED,
                optionService.createOption(questionId, request, currentUser.getId())
        );
    }

    // Options can only be edited by the owner instructor
    @PutMapping("/options/{id}")
    public ResponseEntity<GeneralResponse> updateOption(
            @PathVariable UUID id,
            @RequestBody @Valid OptionRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Option updated successfully",
                HttpStatus.OK,
                optionService.updateOption(id, request, currentUser.getId())
        );
    }

    // Options can only be deleted by the owner instructor
    @DeleteMapping("/options/{id}")
    public ResponseEntity<GeneralResponse> deleteOption(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        optionService.deleteOption(id, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Option deleted successfully",
                HttpStatus.OK,
                null
        );
    }
}
