package org.example.ndemy_backend.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.ndemy_backend.models.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private Role role;
    private Boolean isActive;
    private Boolean isLocked;
    private LocalDateTime createdAt;
    private String photoUrl;
}
