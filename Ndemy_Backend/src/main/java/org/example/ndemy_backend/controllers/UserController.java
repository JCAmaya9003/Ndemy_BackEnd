package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ChangePasswordRequest;
import org.example.ndemy_backend.dto.request.UpdateProfileRequest;
import org.example.ndemy_backend.dto.request.UpdateUserRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.dto.response.UserResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Usuario desactivado correctamente")
                .status(200)
                .time(LocalDateTime.now())
                .build());
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<GeneralResponse> unlockUser(@PathVariable UUID id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Cuenta desbloqueada correctamente")
                .status(200)
                .time(LocalDateTime.now())
                .build());
    }

    // ─────────────────────────────────────────────────────────────────────────────
// UserProfileController.java  —  Endpoints de perfil propio (NUEVO)
// Separado en su propio controller para mantener la separación admin / usuario
// ─────────────────────────────────────────────────────────────────────────────
    @RestController
    @RequestMapping("/api/users/me")
    @RequiredArgsConstructor
    public class UserProfileController {

        private final UserService userService;

        /**
         * GET /api/users/me
         * Retorna el perfil del usuario autenticado.
         * El email se lee directamente del SecurityContext (inyectado por JWT).
         */
        @GetMapping
        public ResponseEntity<UserResponse> getMyProfile(
                @AuthenticationPrincipal User currentUser) {
            return ResponseEntity.ok(userService.getMyProfile(currentUser.getUsername()));
        }

        /**
         * PUT /api/users/me
         * Actualiza nombre y/o foto de perfil.
         */
        @PutMapping
        public ResponseEntity<UserResponse> updateMyProfile(
                @AuthenticationPrincipal User currentUser,
                @RequestBody @Valid UpdateProfileRequest request) {
            return ResponseEntity.ok(
                    userService.updateMyProfile(currentUser.getUsername(), request)
            );
        }

        /**
         * PUT /api/users/me/password
         * Recibe contraseña actual y nueva.
         * Verifica la actual con BCrypt y guarda la nueva hasheada.
         */
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
}
