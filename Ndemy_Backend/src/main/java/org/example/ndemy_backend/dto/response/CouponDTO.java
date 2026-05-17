package org.example.ndemy_backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private UUID id;
    private String code;
    private Integer discountPercent;
    private Integer maxUses;
    private Integer currentUses;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private UUID createdById;
    private String createdByName;
}
