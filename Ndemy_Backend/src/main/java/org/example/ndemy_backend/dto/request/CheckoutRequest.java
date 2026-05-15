package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "El ID del curso es obligatorio")
    private Long courseId;

    private String couponCode;
}
