package org.example.ndemy_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID courseId;
    private String courseTitle;
    private BigDecimal amount;
    private String couponCode;
    private LocalDateTime paidAt;
    private String status;
}
