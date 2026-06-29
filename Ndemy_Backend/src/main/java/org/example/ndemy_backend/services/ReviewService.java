package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.ReviewRequest;
import org.example.ndemy_backend.dto.response.ReviewDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {

    ReviewDTO createReview(UUID courseId, UUID studentId, ReviewRequest request);

    List<ReviewDTO> getReviewsByCourse(UUID courseId);

    ReviewDTO updateReview(UUID reviewId, UUID studentId, ReviewRequest request);

    void deleteReview(UUID reviewId, UUID requesterId, String requesterRole);
}
