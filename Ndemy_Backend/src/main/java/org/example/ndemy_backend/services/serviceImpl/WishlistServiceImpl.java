package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.WishlistItemModel;
import org.example.ndemy_backend.repositories.WishlistItemRepository;
import org.example.ndemy_backend.services.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final CourseRepository courseRepository;

    @Override
    public void addToWishlist(UUID studentId, UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Curso no encontrado");
        }

        if (wishlistItemRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El curso ya está en tu wishlist");
        }

        User student = new User();
        student.setId(studentId);

        Course course = new Course();
        course.setId(courseId);

        WishlistItemModel item = WishlistItemModel.builder()
                .student(student)
                .course(course)
                .build();

        wishlistItemRepository.save(item);
    }

    @Override
    public void removeFromWishlist(UUID studentId, UUID courseId) {
        WishlistItemModel item = wishlistItemRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("El curso no está en tu wishlist"));

        wishlistItemRepository.delete(item);
    }

    @Override
    public List<CourseDTO> getWishlist(UUID studentId) {
        return wishlistItemRepository.findByStudentId(studentId)
                .stream()
                .map(item -> CourseDTO.builder()
                        .id(item.getCourse().getId())
                        .title(item.getCourse().getTitle())
                        .price(item.getCourse().getPrice())
                        .category(item.getCourse().getCategory())
                        .build())
                .toList();
    }
}
