package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ChangePasswordRequest;
import org.example.ndemy_backend.dto.request.UpdateProfileRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.dto.response.UserResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMyProfile(currentUser.getUsername()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid UpdateProfileRequest request) {
        return ResponseEntity.ok(
                userService.updateMyProfile(currentUser.getUsername(), request)
        );
    }

    @PutMapping("/password")
    public ResponseEntity<GeneralResponse> changePassword(
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(currentUser.getUsername(), request);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Contraseña actualizada correctamente")
                .status(200)
                .time(LocalDateTime.now())
                .build());
    }
}