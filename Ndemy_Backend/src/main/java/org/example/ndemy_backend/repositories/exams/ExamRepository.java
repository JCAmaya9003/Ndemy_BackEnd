package org.example.ndemy_backend.repositories.exams;

import org.example.ndemy_backend.models.exams.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    boolean existsByCourseId(UUID courseId);

    Optional<Exam> findByCourseId(UUID courseId);
}
