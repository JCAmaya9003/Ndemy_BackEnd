package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "El rating es obligatorio")
    @Min(value = 1, message = "El rating minimo es 1")
    @Max(value = 5, message = "El rating máximo es 5")
    private Integer rating;

    private String comment;
}
