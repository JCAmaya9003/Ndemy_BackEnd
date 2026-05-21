package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateResponse {
    private UUID id;
    private String courseTitle;
    private String studentName;
    private String certificateCode;
    private LocalDateTime issuedAt;
}
