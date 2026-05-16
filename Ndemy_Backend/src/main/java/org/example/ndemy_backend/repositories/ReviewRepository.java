package org.example.ndemy_backend.repositories;


import org.example.ndemy_backend.models.ReviewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewModel, UUID> {

    List<ReviewModel> findByCourseId(UUID courseId);

    Optional<ReviewModel> findByStudentIdAndCourseId(UUID studentId, UUID courseId);

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    @Query("SELECT AVG(r.rating) FROM ReviewModel r WHERE r.course.id = :courseId")
    Optional<Double> findAverageRatingByCourseId(@Param("courseId") UUID courseId);
}