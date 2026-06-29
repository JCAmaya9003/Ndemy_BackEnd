package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CheckoutRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.services.PaymentService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // POST STUDENT
    @PostMapping("/checkout")
    public ResponseEntity<GeneralResponse> checkout(
            @RequestBody @Valid CheckoutRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedException("Solo los estudiantes pueden realizar pagos");
        }
        return ResponseBuilder.buildResponse(
                "Pago procesado correctamente",
                HttpStatus.CREATED,
                paymentService.checkout(currentUser.getId(), request));
    }

    // GET PAGOS DEL ESTUDIANTE (para reembolso/desinscripcion)
    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyPayments(
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedException("Solo los estudiantes pueden ver sus pagos");
        }
        return ResponseBuilder.buildResponse(
                "Pagos obtenidos correctamente",
                HttpStatus.OK,
                paymentService.getMyPayments(currentUser.getId()));
    }

    // POST REFUND STUDENT
    @PostMapping("/{id}/refund")
    public ResponseEntity<GeneralResponse> refund(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedException("Solo los estudiantes pueden solicitar reembolsos");
        }
        return ResponseBuilder.buildResponse(
                "Reembolso procesado correctamente",
                HttpStatus.OK,
                paymentService.refund(id, currentUser.getId()));
    }
}