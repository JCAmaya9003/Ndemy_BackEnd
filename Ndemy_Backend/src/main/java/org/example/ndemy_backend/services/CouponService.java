package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.CouponRequest;
import org.example.ndemy_backend.dto.response.CouponDTO;
import org.example.ndemy_backend.dto.response.CouponPreviewResponse;
import org.example.ndemy_backend.models.Coupon;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CouponService {

    CouponDTO createCoupon(UUID createdById, CouponRequest request);

    List<CouponDTO> getAllCoupons(UUID requesterId, String requesterRole);

    CouponDTO updateCoupon(UUID couponId, CouponRequest request);

    void deactivateCoupon(UUID couponId);

    BigDecimal applyCoupon(String code, UUID userId, UUID courseId);

    CouponPreviewResponse previewCoupon(String code, UUID userId, UUID courseId);

    Coupon validateAndGetCoupon(String code, UUID userId);

    BigDecimal calculateFinalPrice(BigDecimal originalPrice, Coupon coupon);
}

