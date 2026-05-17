package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.WishlistItemModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistItemRepository extends JpaRepository<WishlistItemModel, UUID> {

    List<WishlistItemModel> findByStudentId(UUID studentId);

    Optional<WishlistItemModel> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
}
