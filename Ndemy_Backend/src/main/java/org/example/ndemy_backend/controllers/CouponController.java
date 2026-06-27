package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.CouponRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.services.CouponService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // POST COUPON ADMIN E INSTRUCTOR
    @PostMapping
    public ResponseEntity<GeneralResponse> createCoupon(
            @RequestBody @Valid CouponRequest request,
            @AuthenticationPrincipal User currentUser) {
        requireAdminOrInstructor(currentUser);
        return ResponseBuilder.buildResponse(
                "Cupon creado correctamente",
                HttpStatus.CREATED,
                couponService.createCoupon(currentUser.getId(), request));
    }

    // GET COUPONS ADMIN VE TODOS, INSTRUCTOR VE SUYOS
    @GetMapping
    public ResponseEntity<GeneralResponse> getAllCoupons(@AuthenticationPrincipal User currentUser) {
        requireAdminOrInstructor(currentUser);
        return ResponseBuilder.buildResponse(
                "Cupones encontrados correctamente",
                HttpStatus.OK,
                couponService.getAllCoupons(currentUser.getId(), currentUser.getRole().name()));
    }

    // PUT COUPON ADMIN E INSTRUCTOR
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateCoupon(
            @PathVariable UUID id,
            @RequestBody @Valid CouponRequest request,
            @AuthenticationPrincipal User currentUser) {
        requireAdminOrInstructor(currentUser);
        return ResponseBuilder.buildResponse(
                "Cupon actualizado correctamente",
                HttpStatus.OK,
                couponService.updateCoupon(id, request));
    }

    // DELETE COUPON ADMIN E INSTRUCTOR
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deactivateCoupon(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        requireAdminOrInstructor(currentUser);
        couponService.deactivateCoupon(id);
        return ResponseBuilder.buildResponse(
                "Cupon desactivado correctamente",
                HttpStatus.OK,
                null);
    }

    // GET PREVIEW: cualquier usuario autenticado puede validar un cupón
    // y ver el precio con descuento antes de pagar
    @GetMapping("/preview")
    public ResponseEntity<GeneralResponse> previewCoupon(
            @RequestParam String code,
            @RequestParam UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Cupon válido",
                HttpStatus.OK,
                couponService.previewCoupon(code, currentUser.getId(), courseId));
    }

    private void requireAdminOrInstructor(User user) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.INSTRUCTOR) {
            throw new UnauthorizedException("No tienes permiso para gestionar cupones");
        }
    }
}
