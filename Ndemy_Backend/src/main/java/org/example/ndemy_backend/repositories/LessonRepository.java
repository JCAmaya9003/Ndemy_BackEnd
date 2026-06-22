package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(UUID moduleId);

    int countByModuleCourseId(UUID courseId);

    boolean existsByModuleIdAndOrderIndex(UUID moduleId, Integer orderIndex);
    List<Lesson> findByModuleIdAndOrderIndexGreaterThanEqual(UUID moduleId, Integer orderIndex);
    List<Lesson> findByModuleIdAndOrderIndexBetween(UUID moduleId, Integer start, Integer end);

    @Query("SELECT MAX(l.orderIndex) FROM Lesson l WHERE l.module.id = :moduleId")
    Optional<Integer> findMaxOrderIndexByModuleId(@Param("moduleId") UUID moduleId);
    boolean existsByModuleCoursIdAndInstructorId(UUID courseId, UUID instructorId);
}
