package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRequest {
    @NotBlank(message = "Course must have a title")
    @Size(max = 200, message = "Course title exceeds character limit, max: 200 characters")
    private String title;

    private String description;

    @NotNull(message = "Course must have a price")
    @PositiveOrZero(message = "Course price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @Size(max = 100, message = "Course category exceeds character limit, max: 100 characters")
    private String category;

    @Positive(message = "Course duration in hours must be greater than 0")
    private Integer durationHours;

    @Size(max = 500, message = "Course thumbnail url exceeds character limit, max: 500 characters")
    private String thumbnailUrl;
}
