package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ReviewRequest;
import org.example.ndemy_backend.dto.response.ReviewDTO;
import org.example.ndemy_backend.exceptions.AlreadyReviewedException;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.ReviewModel;
import org.example.ndemy_backend.repositories.ReviewRepository;
import org.example.ndemy_backend.services.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewDTO createReview(UUID courseId, UUID studentId, ReviewRequest request) {

        if (reviewRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyReviewedException("Ya dejaste una reseña en este curso");
        }

        User student = new User();
        student.setId(studentId);

        Course course = new Course();
        course.setId(courseId);

        ReviewModel review = ReviewModel.builder()
                .student(student)
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        ReviewModel saved = reviewRepository.save(review);
        return mapToDTO(saved);
    }

    @Override
    public List<ReviewDTO> getReviewsByCourse(UUID courseId) {
        return reviewRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ReviewDTO updateReview(UUID reviewId, UUID studentId, ReviewRequest request) {
        ReviewModel review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        if (!review.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("No tienes permiso para editar esta reseña");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        ReviewModel updated = reviewRepository.save(review);
        return mapToDTO(updated);
    }

    @Override
    public void deleteReview(UUID reviewId, UUID requesterId, String requesterRole) {
        ReviewModel review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        boolean isAdmin = "ADMIN".equals(requesterRole);
        boolean isAuthor = review.getStudent().getId().equals(requesterId);

        if (!isAdmin && !isAuthor) {
            throw new ResourceNotFoundException("No tienes permiso para eliminar esta reseña");
        }

        reviewRepository.delete(review);
    }

    private ReviewDTO mapToDTO(ReviewModel review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .studentId(review.getStudent().getId())
                .studentName(review.getStudent().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
