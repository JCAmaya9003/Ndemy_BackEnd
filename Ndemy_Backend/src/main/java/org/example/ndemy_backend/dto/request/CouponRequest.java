package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {

    @NotBlank(message = "El codigo del cupon es obligatorio")
    @Size(max = 50, message = "El codigo no puede superar los 50 caracteres")
    private String code;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @Min(value = 1, message = "El descuento minimo es 1%")
    @Max(value = 100, message = "El descuento maximo es 100%")
    private Integer discountPercent;

    @NotNull(message = "El maximo de usos es obligatorio")
    @Min(value = 1, message = "El maximo de usos debe ser al menos 1")
    private Integer maxUses;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDateTime expiresAt;
}
