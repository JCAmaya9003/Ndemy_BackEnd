package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ReviewRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.services.ReviewService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //STUDENT POST
    @PostMapping("/courses/{courseId}/reviews")
    public ResponseEntity<GeneralResponse> createReview(
            @PathVariable UUID courseId,
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.STUDENT) {
            throw new UnauthorizedException("Solo los estudiantes pueden dejar reseñas");
        }
        return ResponseBuilder.buildResponse(
                "Reseña creada correctamente",
                HttpStatus.CREATED,
                reviewService.createReview(courseId, currentUser.getId(), request));
    }

    //PUBLICO GET
    @GetMapping("/courses/{courseId}/reviews")
    public ResponseEntity<GeneralResponse> getReviews(@PathVariable UUID courseId) {
        return ResponseBuilder.buildResponse(
                "Reseñas encontradas correctamente",
                HttpStatus.OK,
                reviewService.getReviewsByCourse(courseId));
    }

    //PUT REVIEW DEL AUTOR
    @PutMapping("/reviews/{id}")
    public ResponseEntity<GeneralResponse> updateReview(
            @PathVariable UUID id,
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Reseña actualizada correctamente",
                HttpStatus.OK,
                reviewService.updateReview(id, currentUser.getId(), request));
    }

    // DELETE REVIEW AUTOR O ADMIN
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<GeneralResponse> deleteReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        reviewService.deleteReview(id, currentUser.getId(), currentUser.getRole().name());
        return ResponseBuilder.buildResponse(
                "Reseña eliminada correctamente",
                HttpStatus.OK,
                null);
    }
}