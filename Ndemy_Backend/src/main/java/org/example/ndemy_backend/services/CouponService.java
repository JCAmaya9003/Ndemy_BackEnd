package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.CouponRequest;
import org.example.ndemy_backend.dto.response.CouponDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CouponService {

    CouponDTO createCoupon(UUID createdById, CouponRequest request);

    List<CouponDTO> getAllCoupons(UUID requesterId, String requesterRole);

    CouponDTO updateCoupon(UUID couponId, CouponRequest request);

    void deactivateCoupon(UUID couponId);

    BigDecimal applyCoupon(String code, UUID userId, UUID courseId);
}

