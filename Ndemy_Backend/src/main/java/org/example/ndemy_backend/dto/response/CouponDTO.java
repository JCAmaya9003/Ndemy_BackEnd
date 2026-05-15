package org.example.ndemy_backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private Long id;
    private String code;
    private Integer discountPercent;
    private Integer maxUses;
    private Integer currentUses;
    private LocalDateTime expiresAt;
    private Boolean isActive;
    private Long createdById;
    private String createdByName;
}
