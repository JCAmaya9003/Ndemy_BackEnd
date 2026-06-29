package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "El ID del curso es obligatorio")
    private UUID courseId;

    private String couponCode;
}
