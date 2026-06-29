package org.example.ndemy_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDTO {

    private BigDecimal totalRevenue;
    private BigDecimal currentMonthRevenue;
    private List<CourseRevenueDTO> courseBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseRevenueDTO {
        private UUID courseId;
        private String courseTitle;
        private BigDecimal revenue;
        private Integer studentsPaid;
    }
}