package org.example.ndemy_backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "max_uses", nullable = false)
    private Integer maxUses;

    @Column(name = "current_uses", nullable = false)
    private Integer currentUses;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    //se activara una vez exista la clase User
   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;*/
}

