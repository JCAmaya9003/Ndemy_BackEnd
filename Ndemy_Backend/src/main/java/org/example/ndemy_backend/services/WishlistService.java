package org.example.ndemy_backend.services;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    void addToWishlist(UUID studentId, UUID courseId);

    void removeFromWishlist(UUID studentId, UUID courseId);

    List<CourseDTO> getWishlist(UUID studentId);
}
