package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.response.CertificateResponse;

import java.util.List;
import java.util.UUID;

public interface CertificateService {
    CertificateResponse verifyCertificate(String certificateCode);
    List<CertificateResponse> getStudentCertificates(UUID studentId);
}
