package org.example.ndemy_backend.repositories.exams;

import org.example.ndemy_backend.models.exams.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByExamIdOrderByOrderIndexAsc(UUID examId);

    boolean existsByExamIdAndOrderIndex(UUID examId, Integer orderIndex);
    List<Question> findByExamIdAndOrderIndexGreaterThanEqual(UUID examId, Integer orderIndex);
    List<Question> findByExamIdAndOrderIndexBetween(UUID examId, Integer start, Integer end);

    @Query("SELECT MAX(q.orderIndex) FROM Question q WHERE q.exam.id = :examId")
    Optional<Integer> findMaxOrderIndexByExamId(@Param("examId") UUID examId);

    int countByExamId(UUID examId);
}
