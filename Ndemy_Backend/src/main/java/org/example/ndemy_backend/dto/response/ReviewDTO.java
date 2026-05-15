package org.example.ndemy_backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Long courseId;
    private Long studentId;
    private String studentName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
