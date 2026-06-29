package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.CheckoutRequest;
import org.example.ndemy_backend.dto.response.PaymentDTO;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentDTO checkout(UUID studentId, CheckoutRequest request);

    PaymentDTO refund(UUID paymentId, UUID studentId);

    List<PaymentDTO> getMyPayments(UUID studentId);
}
