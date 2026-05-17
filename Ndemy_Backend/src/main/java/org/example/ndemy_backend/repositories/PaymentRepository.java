package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.PaymentRecordModel;
import org.example.ndemy_backend.models.enums.PaymentStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentRecordModel, UUID> {

    boolean existsByStudentIdAndCourseIdAndStatus(UUID studentId, UUID courseId, PaymentStatusModel status);

    List<PaymentRecordModel> findByStudentId(UUID studentId);

    @Query("SELECT SUM(p.amount) FROM PaymentRecordModel p WHERE p.course.instructor.id = :instructorId AND p.status = 'COMPLETED'")
    BigDecimal findTotalRevenueByInstructorId(@Param("instructorId") UUID instructorId);

    @Query("SELECT SUM(p.amount) FROM PaymentRecordModel p WHERE p.course.instructor.id = :instructorId AND p.status = 'COMPLETED' AND p.paidAt >= :from")
    BigDecimal findMonthlyRevenueByInstructorId(@Param("instructorId") UUID instructorId, @Param("from") LocalDateTime from);

    @Query("SELECT SUM(p.amount) FROM PaymentRecordModel p WHERE p.status = 'COMPLETED'")
    BigDecimal findTotalPlatformRevenue();
}
