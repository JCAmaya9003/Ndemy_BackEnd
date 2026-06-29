package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CouponRequest;
import org.example.ndemy_backend.dto.response.CouponDTO;
import org.example.ndemy_backend.dto.response.CouponPreviewResponse;
import org.example.ndemy_backend.exceptions.*;
import org.example.ndemy_backend.models.Coupon;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.repositories.CouponRepository;
import org.example.ndemy_backend.repositories.CouponUsageRepository;
import org.example.ndemy_backend.repositories.CourseRepository;
import org.example.ndemy_backend.repositories.UserRepository;
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
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public CouponDTO createCoupon(UUID createdById, CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new DuplicateCouponCodeException("Ya existe un cupón con ese código");
        }

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .discountPercent(request.getDiscountPercent())
                .maxUses(request.getMaxUses())
                .currentUses(0)
                .expiresAt(request.getExpiresAt())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        Coupon saved = couponRepository.save(coupon);
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
    public CouponDTO updateCoupon(UUID couponId, CouponRequest request, UUID requesterId, String requesterRole) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado"));

        verifyOwnerOrAdmin(coupon, requesterId, requesterRole);

        if (coupon.getCurrentUses() > 0) {
            throw new CouponNotEditableException("No se puede editar un cupón que ya tiene usos registrados");
        }
        // evita el 500 por código duplicado
        if (!coupon.getCode().equals(request.getCode()) && couponRepository.existsByCode(request.getCode())) {
            throw new DuplicateCouponCodeException("Ya existe un cupón con ese código");
        }

        coupon.setCode(request.getCode());
        coupon.setDiscountPercent(request.getDiscountPercent());
        coupon.setMaxUses(request.getMaxUses());
        coupon.setExpiresAt(request.getExpiresAt());
        return mapToDTO(couponRepository.save(coupon));
    }

    @Override
    public void deactivateCoupon(UUID couponId, UUID requesterId, String requesterRole) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado"));
        verifyOwnerOrAdmin(coupon, requesterId, requesterRole);
        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    private void verifyOwnerOrAdmin(Coupon coupon, UUID requesterId, String requesterRole) {
        if (!"ADMIN".equals(requesterRole) && !coupon.getCreatedBy().getId().equals(requesterId)) {
            throw new UnauthorizedException("No tienes permiso para gestionar este cupón");
        }
    }

    @Override
    public BigDecimal applyCoupon(String code, UUID userId, UUID courseId) {
        Coupon coupon = validateAndGetCoupon(code, userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        return calculateFinalPrice(course.getPrice(), coupon);
    }

    @Override
    public CouponPreviewResponse previewCoupon(String code, UUID userId, UUID courseId) {
        Coupon coupon = validateAndGetCoupon(code, userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        BigDecimal originalPrice = course.getPrice();
        BigDecimal finalPrice = calculateFinalPrice(originalPrice, coupon);
        BigDecimal discountAmount = originalPrice.subtract(finalPrice)
                .setScale(2, RoundingMode.HALF_UP);

        return CouponPreviewResponse.builder()
                .code(coupon.getCode())
                .discountPercent(coupon.getDiscountPercent())
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }

    @Override
    public Coupon validateAndGetCoupon(String code, UUID userId) {
        Coupon coupon = couponRepository.findByCode(code)
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

    @Override
    public BigDecimal calculateFinalPrice(BigDecimal originalPrice, Coupon coupon) {
        BigDecimal discount = BigDecimal.valueOf(coupon.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = originalPrice.multiply(discount);
        return originalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private CouponDTO mapToDTO(Coupon coupon) {
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