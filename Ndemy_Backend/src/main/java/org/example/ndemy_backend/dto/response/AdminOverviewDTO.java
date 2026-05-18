package org.example.ndemy_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewDTO {

    private Long totalStudents;
    private Long totalInstructors;
    private Long totalAdmins;
    private Long totalPublishedCourses;
    private Long totalActiveEnrollments;
    private BigDecimal totalPlatformRevenue;
    private List<CourseRankDTO> topCoursesByEnrollments;
    private List<CourseRankDTO> topCoursesByRevenue;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseRankDTO {
        private UUID courseId;
        private String courseTitle;
        private String instructorName;
        private Long enrollments;
        private BigDecimal revenue;
    }
}
