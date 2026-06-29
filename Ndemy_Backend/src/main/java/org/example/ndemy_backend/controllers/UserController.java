package org.example.ndemy_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.UpdateUserRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.dto.response.UserResponse;
import org.example.ndemy_backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
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

    @PutMapping("/{id}/lock")
    public ResponseEntity<GeneralResponse> lockUser(@PathVariable UUID id) {
        userService.lockUser(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Usuario baneado correctamente")
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
}
