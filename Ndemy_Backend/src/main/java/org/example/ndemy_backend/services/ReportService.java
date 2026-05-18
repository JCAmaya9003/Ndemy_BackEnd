package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.response.AdminOverviewDTO;
import org.example.ndemy_backend.dto.response.RevenueReportDTO;

import java.util.UUID;

public interface ReportService {

    RevenueReportDTO getInstructorRevenueReport(UUID instructorId);

    AdminOverviewDTO getAdminOverview();
}
