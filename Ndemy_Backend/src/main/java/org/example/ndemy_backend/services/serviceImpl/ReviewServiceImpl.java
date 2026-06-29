package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ReviewRequest;
import org.example.ndemy_backend.dto.response.ReviewDTO;
import org.example.ndemy_backend.exceptions.AlreadyReviewedException;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Review;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.repositories.CourseRepository;
import org.example.ndemy_backend.repositories.ReviewRepository;
import org.example.ndemy_backend.repositories.UserRepository;
import org.example.ndemy_backend.services.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    public ReviewDTO createReview(UUID courseId, UUID studentId, ReviewRequest request) {

        if (reviewRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyReviewedException("Ya dejaste una reseña en este curso");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        Review review = Review.builder()
                .student(student)
                .course(course)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
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
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        if (!review.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedException("No tienes permiso para editar esta reseña");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);
        return mapToDTO(updated);
    }

    @Override
    public void deleteReview(UUID reviewId, UUID requesterId, String requesterRole) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        boolean isAdmin = "ADMIN".equals(requesterRole);
        boolean isAuthor = review.getStudent().getId().equals(requesterId);

        if (!isAdmin && !isAuthor) {
            throw new UnauthorizedException("No tienes permiso para eliminar esta reseña");
        }

        reviewRepository.delete(review);
    }

    private ReviewDTO mapToDTO(Review review) {
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