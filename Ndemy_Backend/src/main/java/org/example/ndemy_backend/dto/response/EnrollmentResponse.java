package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentResponse {
    private UUID id;
    private UUID courseId;
    private String courseTitle;
    private Boolean isActive;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}
