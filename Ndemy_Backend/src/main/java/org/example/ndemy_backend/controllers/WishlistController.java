package org.example.ndemy_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.services.WishlistService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // POST STUDENT
    @PostMapping("/{courseId}")
    public ResponseEntity<GeneralResponse> addToWishlist(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        requireStudent(currentUser);
        wishlistService.addToWishlist(currentUser.getId(), courseId);
        return ResponseBuilder.buildResponse(
                "Curso agregado a tu wishlist",
                HttpStatus.CREATED,
                null);
    }

    // DELETE STUDENT
    @DeleteMapping("/{courseId}")
    public ResponseEntity<GeneralResponse> removeFromWishlist(
            @PathVariable UUID courseId,
            @AuthenticationPrincipal User currentUser) {
        requireStudent(currentUser);
        wishlistService.removeFromWishlist(currentUser.getId(), courseId);
        return ResponseBuilder.buildResponse(
                "Curso eliminado de tu wishlist",
                HttpStatus.OK,
                null);
    }

    // GET STUDENT
    @GetMapping
    public ResponseEntity<GeneralResponse> getWishlist(@AuthenticationPrincipal User currentUser) {
        requireStudent(currentUser);
        return ResponseBuilder.buildResponse(
                "Wishlist encontrada correctamente",
                HttpStatus.OK,
                wishlistService.getWishlist(currentUser.getId()));
    }

    private void requireStudent(User user) {
        if (user.getRole() != Role.STUDENT) {
            throw new UnauthorizedException("Solo los estudiantes pueden usar la wishlist");
        }
    }
}
