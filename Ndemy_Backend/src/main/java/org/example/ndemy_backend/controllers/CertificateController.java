package org.example.ndemy_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.services.CertificateService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    // Verification of certificate
    @GetMapping("/verify/{code}")
    public ResponseEntity<GeneralResponse> verifyCertificate(
            @PathVariable String code) {
        return ResponseBuilder.buildResponse(
                "Certificate verified successfully",
                HttpStatus.OK,
                certificateService.verifyCertificate(code)
        );
    }
}
