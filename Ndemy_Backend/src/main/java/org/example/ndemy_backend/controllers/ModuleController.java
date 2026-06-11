package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ModuleRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.ModuleService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    // orderIndex is optional in module requests
    // if not provided, it will be assigned automatically at the end
    // if the index already exists, existing modules will be shifted to the right
    // this allows inserting modules between existing ones

    // Modules can only be created by instructors for their own courses
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<GeneralResponse> createModule(
            @PathVariable UUID courseId,
            @RequestBody @Valid ModuleRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Module created successfully",
                HttpStatus.CREATED,
                moduleService.createModule(courseId, request, currentUser.getId())
        );
    }

    // Modules can only be edited by the owner instructor
    @PutMapping("/modules/{id}")
    public ResponseEntity<GeneralResponse> updateModule(
            @PathVariable UUID id,
            @RequestBody @Valid ModuleRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Module updated successfully",
                HttpStatus.OK,
                moduleService.updateModule(id, request, currentUser.getId())
        );
    }

    // Modules can only be deleted by the owner instructor
    @DeleteMapping("/modules/{id}")
    public ResponseEntity<GeneralResponse> deleteModule(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        moduleService.deleteModule(id, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Module deleted successfully",
                HttpStatus.OK,
                null
        );
    }
}
