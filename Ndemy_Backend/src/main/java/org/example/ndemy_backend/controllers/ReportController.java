package org.example.ndemy_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.services.ReportService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // GET COURSE REPORT INSTRUCTOR
    @GetMapping("/instructor/reports/revenue")
    public ResponseEntity<GeneralResponse> getInstructorRevenueReport(
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.INSTRUCTOR) {
            throw new UnauthorizedException("Solo los instructores pueden ver este reporte");
        }
        return ResponseBuilder.buildResponse(
                "Reporte de ingresos generado correctamente",
                HttpStatus.OK,
                reportService.getInstructorRevenueReport(currentUser.getId()));
    }

    // GET /admin/reports/overview -> ADMIN
    @GetMapping("/admin/reports/overview")
    public ResponseEntity<GeneralResponse> getAdminOverview(
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Solo los administradores pueden ver este reporte");
        }
        return ResponseBuilder.buildResponse(
                "Reporte global generado correctamente",
                HttpStatus.OK,
                reportService.getAdminOverview());
    }
}
