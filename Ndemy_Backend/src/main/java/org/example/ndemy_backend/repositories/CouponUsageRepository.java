package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, UUID> {

    boolean existsByCouponIdAndUserId(UUID couponId, UUID userId);
}