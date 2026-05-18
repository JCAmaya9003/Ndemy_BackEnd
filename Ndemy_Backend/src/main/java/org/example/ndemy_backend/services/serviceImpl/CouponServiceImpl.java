package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CouponRequest;
import org.example.ndemy_backend.dto.response.CouponDTO;
import org.example.ndemy_backend.exceptions.*;
import org.example.ndemy_backend.models.CouponModel;
import org.example.ndemy_backend.repositories.CouponRepository;
import org.example.ndemy_backend.repositories.CouponUsageRepository;
import org.example.ndemy_backend.services.CouponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    @Override
    public CouponDTO createCoupon(UUID createdById, CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new ResourceNotFoundException("Ya existe un cupón con ese código");
        }

        User createdBy = new User();
        createdBy.setId(createdById);

        CouponModel coupon = CouponModel.builder()
                .code(request.getCode())
                .discountPercent(request.getDiscountPercent())
                .maxUses(request.getMaxUses())
                .currentUses(0)
                .expiresAt(request.getExpiresAt())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        CouponModel saved = couponRepository.save(coupon);
        return mapToDTO(saved);
    }

    @Override
    public List<CouponDTO> getAllCoupons(UUID requesterId, String requesterRole) {
        return couponRepository.findAll()
                .stream()
                .filter(c -> "ADMIN".equals(requesterRole) || c.getCreatedBy().getId().equals(requesterId))
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public CouponDTO updateCoupon(UUID couponId, CouponRequest request) {
        CouponModel coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado"));

        if (coupon.getCurrentUses() > 0) {
            throw new ResourceNotFoundException("No se puede editar un cupón que ya tiene usos registrados");
        }

        coupon.setCode(request.getCode());
        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setMaxUses(request.getMaxUses());
        coupon.setExpiresAt(request.getExpiresAt());

        CouponModel updated = couponRepository.save(coupon);
        return mapToDTO(updated);
    }

    @Override
    public void deactivateCoupon(UUID couponId) {
        CouponModel coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado"));

        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    @Override
    public BigDecimal applyCoupon(String code, UUID userId, UUID courseId) {
        CouponModel coupon = validateAndGetCoupon(code, userId);
        return calculateFinalPrice(courseId, coupon);
    }

    public CouponModel validateAndGetCoupon(String code, UUID userId) {
        CouponModel coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado"));

        if (!coupon.getIsActive()) {
            throw new CouponInactiveException("El cupón está inactivo");
        }

        if (coupon.getCurrentUses() >= coupon.getMaxUses()) {
            throw new CouponExhaustedException("El cupón ha alcanzado su límite de usos");
        }

        if (coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException("El cupón ha vencido");
        }

        if (couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(), userId)) {
            throw new CouponAlreadyUsedException("Ya usaste este cupón anteriormente");
        }

        return coupon;
    }

    public BigDecimal calculateFinalPrice(BigDecimal originalPrice, CouponModel coupon) {
        BigDecimal discount = BigDecimal.valueOf(coupon.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = originalPrice.multiply(discount);
        return originalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private CouponDTO mapToDTO(CouponModel coupon) {
        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountPercent(coupon.getDiscountPercent())
                .maxUses(coupon.getMaxUses())
                .currentUses(coupon.getCurrentUses())
                .expiresAt(coupon.getExpiresAt())
                .isActive(coupon.getIsActive())
                .createdById(coupon.getCreatedBy().getId())
                .createdByName(coupon.getCreatedBy().getName())
                .build();
    }
}
