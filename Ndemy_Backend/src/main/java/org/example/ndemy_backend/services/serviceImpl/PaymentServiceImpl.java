package org.example.ndemy_backend.services.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CheckoutRequest;
import org.example.ndemy_backend.dto.response.PaymentDTO;
import org.example.ndemy_backend.exceptions.AlreadyEnrolledException;
import org.example.ndemy_backend.exceptions.PaymentNotRefundableException;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.*;
import org.example.ndemy_backend.models.enums.PaymentStatus;
import org.example.ndemy_backend.repositories.CouponRepository;
import org.example.ndemy_backend.repositories.CouponUsageRepository;
import org.example.ndemy_backend.repositories.CourseRepository;
import org.example.ndemy_backend.repositories.PaymentRepository;
import org.example.ndemy_backend.repositories.UserRepository;
import org.example.ndemy_backend.services.CouponService;
import org.example.ndemy_backend.services.EnrollmentService;
import org.example.ndemy_backend.services.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;

    @Override
    @Transactional
    public PaymentDTO checkout(UUID studentId, CheckoutRequest request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        if (paymentRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, course.getId(), PaymentStatus.COMPLETED)) {
            throw new AlreadyEnrolledException("Ya tienes acceso a este curso");
        }

        BigDecimal finalPrice = course.getPrice();
        Coupon couponUsed = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            couponUsed = couponService.validateAndGetCoupon(request.getCouponCode(), studentId);
            finalPrice = couponService.calculateFinalPrice(course.getPrice(), couponUsed);
        }

        PaymentRecord payment = PaymentRecord.builder()
                .student(student)
                .course(course)
                .amount(finalPrice)
                .coupon(couponUsed)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        payment.setStatus(PaymentStatus.COMPLETED);
        PaymentRecord saved = paymentRepository.save(payment);

        if (couponUsed != null) {
            CouponUsage usage = CouponUsage.builder()
                    .coupon(couponUsed)
                    .user(student)
                    .course(course)
                    .build();
            couponUsageRepository.save(usage);

            couponUsed.setCurrentUses(couponUsed.getCurrentUses() + 1);
            couponRepository.save(couponUsed);
        }

        enrollmentService.enroll(studentId, course.getId());

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public PaymentDTO refund(UUID paymentId, UUID studentId) {
        PaymentRecord payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        if (!payment.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedException("No tienes permiso para reembolsar este pago");
        }

        if (!PaymentStatus.COMPLETED.equals(payment.getStatus())) {
            throw new PaymentNotRefundableException("Solo se pueden reembolsar pagos completados");
        }

        enrollmentService.deactivateEnrollment(studentId, payment.getCourse().getId());

        payment.setStatus(PaymentStatus.REFUNDED);
        PaymentRecord saved = paymentRepository.save(payment);

        return mapToDTO(saved);
    }

    private PaymentDTO mapToDTO(PaymentRecord payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .studentId(payment.getStudent().getId())
                .studentName(payment.getStudent().getName())
                .courseId(payment.getCourse().getId())
                .courseTitle(payment.getCourse().getTitle())
                .amount(payment.getAmount())
                .couponCode(payment.getCoupon() != null ? payment.getCoupon().getCode() : null)
                .paidAt(payment.getPaidAt())
                .status(payment.getStatus().name())
                .build();
    }
}