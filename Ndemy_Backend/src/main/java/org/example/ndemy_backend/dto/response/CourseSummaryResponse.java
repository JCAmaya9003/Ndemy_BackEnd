package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryResponse {
    private UUID id;
    private String title;
    private BigDecimal price;
    private String category;
    private String instructorName;
    private String thumbnailUrl;
}
