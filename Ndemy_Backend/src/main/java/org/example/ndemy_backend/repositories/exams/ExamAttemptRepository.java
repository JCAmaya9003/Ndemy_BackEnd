package org.example.ndemy_backend.repositories.exams;

import org.example.ndemy_backend.models.exams.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {
    Optional<ExamAttempt> findByStudentIdAndExamId(UUID studentId, UUID examId);
    
    boolean existsByStudentIdAndExamId(UUID studentId, UUID examId);
}
