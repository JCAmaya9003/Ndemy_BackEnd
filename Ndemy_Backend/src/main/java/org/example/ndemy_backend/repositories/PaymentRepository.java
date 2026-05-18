package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.PaymentRecord;
import org.example.ndemy_backend.models.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentRecord, UUID> {

    boolean existsByStudentIdAndCourseIdAndStatus(UUID studentId, UUID courseId, PaymentStatus status);

    List<PaymentRecord> findByStudentId(UUID studentId);

    @Query("SELECT SUM(p.amount) FROM PaymentRecord p WHERE p.course.instructor.id = :instructorId AND p.status = 'COMPLETED'")
    BigDecimal findTotalRevenueByInstructorId(@Param("instructorId") UUID instructorId);

    @Query("SELECT SUM(p.amount) FROM PaymentRecord p WHERE p.course.instructor.id = :instructorId AND p.status = 'COMPLETED' AND p.paidAt >= :from")
    BigDecimal findMonthlyRevenueByInstructorId(@Param("instructorId") UUID instructorId, @Param("from") LocalDateTime from);

    @Query("SELECT SUM(p.amount) FROM PaymentRecord p WHERE p.status = 'COMPLETED'")
    BigDecimal findTotalPlatformRevenue();

    @Query("SELECT p.course.id, p.course.title, p.course.instructor.name, SUM(p.amount), COUNT(p) " +
            "FROM PaymentRecord p WHERE p.course.instructor.id = :instructorId AND p.status = 'COMPLETED' " +
            "GROUP BY p.course.id, p.course.title, p.course.instructor.name")
    List<Object[]> findCourseRevenueByInstructor(@Param("instructorId") UUID instructorId);

    @Query("SELECT p.course.id, p.course.title, p.course.instructor.name, SUM(p.amount) " +
            "FROM PaymentRecord p WHERE p.status = 'COMPLETED' " +
            "GROUP BY p.course.id, p.course.title, p.course.instructor.name " +
            "ORDER BY SUM(p.amount) DESC " +
            "LIMIT 5")
    List<Object[]> findTop5CoursesByRevenue();
}
