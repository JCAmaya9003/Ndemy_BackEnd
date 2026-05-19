package org.example.ndemy_backend.repositories;

import org.example.ndemy_backend.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Optional<Enrollment> findByStudentIdAndCourseIdAndIsActiveTrue(UUID studentId, UUID courseId);

    List<Enrollment> findByStudentIdAndIsActiveTrue(UUID studentId);
}
