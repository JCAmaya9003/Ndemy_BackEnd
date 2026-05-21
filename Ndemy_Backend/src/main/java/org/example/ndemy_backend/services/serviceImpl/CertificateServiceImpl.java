package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.CertificateResponse;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.models.Certificate;
import org.example.ndemy_backend.repositories.CertificateRepository;
import org.example.ndemy_backend.services.CertificateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository certificateRepository;

    @Override
    public CertificateResponse verifyCertificate(String certificateCode) {
        Certificate certificate = certificateRepository.findByCertificateCode(certificateCode)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        return toCertificateResponse(certificate);
    }

    @Override
    public List<CertificateResponse> getStudentCertificates(UUID studentId) {
        /* when the user repository is ready, uncomment this
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
         */

        return certificateRepository.findByStudentId(studentId)
                .stream()
                .map(this::toCertificateResponse)
                .toList();
    }

    private CertificateResponse toCertificateResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .courseTitle(certificate.getCourse().getTitle())
                // .studentName(certificate.getStudent().getName()) // when the user repository is ready, uncomment this
                .certificateCode(certificate.getCertificateCode())
                .issuedAt(certificate.getIssuedAt())
                .build();
    }
}
