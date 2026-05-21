package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    List<Module> findByCourseIdOrderByOrderIndexAsc(UUID courseId);

    boolean existsByCourseIdAndOrderIndex(UUID courseId, Integer orderIndex);
    List<Module> findByCourseIdAndOrderIndexGreaterThanEqual(UUID courseId, Integer orderIndex);

    @Query("SELECT MAX(m.orderIndex) FROM Module m WHERE m.course.id = :courseId")
    Optional<Integer> findMaxOrderIndexByCourseId(@Param("courseId") UUID courseId);

}
