package org.example.ndemy_backend.dto.response;

import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDetailResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer durationHours;
    private String thumbnailUrl;
    private Boolean isPublished;
    private String instructorName;
    private LocalDateTime createdDate;
    private List<ModuleResponse> modules;
}
