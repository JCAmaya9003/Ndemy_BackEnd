package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.AdminOverviewDTO;
import org.example.ndemy_backend.dto.response.RevenueReportDTO;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.repositories.PaymentRepository;
import org.example.ndemy_backend.services.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    public RevenueReportDTO getInstructorRevenueReport(UUID instructorId) {

        BigDecimal totalRevenue = paymentRepository
                .findTotalRevenueByInstructorId(instructorId);

        BigDecimal monthlyRevenue = paymentRepository
                .findMonthlyRevenueByInstructorId(
                        instructorId,
                        LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay()
                );

        List<RevenueReportDTO.CourseRevenueDTO> courseBreakdown = paymentRepository
                .findCourseRevenueByInstructor(instructorId)
                .stream()
                .map(row -> RevenueReportDTO.CourseRevenueDTO.builder()
                        .courseId((UUID) row[0])
                        .courseTitle((String) row[1])
                        .revenue((BigDecimal) row[2])
                        .studentsPaid(((Long) row[3]).intValue())
                        .build())
                .toList();

        return RevenueReportDTO.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .currentMonthRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                .courseBreakdown(courseBreakdown)
                .build();
    }

    @Override
    public AdminOverviewDTO getAdminOverview() {

        Long totalStudents = userRepository.countByRole(Role.STUDENT);
        Long totalInstructors = userRepository.countByRole(Role.INSTRUCTOR);
        Long totalAdmins = userRepository.countByRole(Role.ADMIN);
        Long totalPublishedCourses = courseRepository.countByIsPublishedTrue();
        Long totalActiveEnrollments = enrollmentRepository.countByIsActiveTrue();
        BigDecimal totalRevenue = paymentRepository.findTotalPlatformRevenue();

        List<AdminOverviewDTO.CourseRankDTO> topByEnrollments = enrollmentRepository
                .findTop5CoursesByEnrollments()
                .stream()
                .map(row -> AdminOverviewDTO.CourseRankDTO.builder()
                        .courseId((UUID) row[0])
                        .courseTitle((String) row[1])
                        .instructorName((String) row[2])
                        .enrollments((Long) row[3])
                        .build())
                .toList();

        List<AdminOverviewDTO.CourseRankDTO> topByRevenue = paymentRepository
                .findTop5CoursesByRevenue()
                .stream()
                .map(row -> AdminOverviewDTO.CourseRankDTO.builder()
                        .courseId((UUID) row[0])
                        .courseTitle((String) row[1])
                        .instructorName((String) row[2])
                        .revenue((BigDecimal) row[3])
                        .build())
                .toList();

        return AdminOverviewDTO.builder()
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalAdmins(totalAdmins)
                .totalPublishedCourses(totalPublishedCourses)
                .totalActiveEnrollments(totalActiveEnrollments)
                .totalPlatformRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .topCoursesByEnrollments(topByEnrollments)
                .topCoursesByRevenue(topByRevenue)
                .build();
    }
}
