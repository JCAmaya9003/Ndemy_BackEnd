package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressResponse {
    private UUID studentId;
    private String studentName;
    private String studentEmail;
    private Double progress;
    private Boolean isCompleted;
    private LocalDateTime enrolledAt;
}
