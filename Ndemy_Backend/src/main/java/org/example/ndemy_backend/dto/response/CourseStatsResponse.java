package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatsResponse {
    private UUID courseId;
    private String title;
    private String category;
    private BigDecimal price;
    private String thumbnailUrl;
    private Boolean isPublished;
    private Integer enrolledCount;
    private Double averageRating;
    private BigDecimal totalRevenue;
}
