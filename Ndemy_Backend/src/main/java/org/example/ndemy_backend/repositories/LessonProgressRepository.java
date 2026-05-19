package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {
    int countByEnrollmentId(UUID enrollmentId);

    boolean existsByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);
}
