package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.response.CourseSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    void addToWishlist(UUID studentId, UUID courseId);

    void removeFromWishlist(UUID studentId, UUID courseId);

    List<CourseSummaryResponse> getWishlist(UUID studentId);
}
