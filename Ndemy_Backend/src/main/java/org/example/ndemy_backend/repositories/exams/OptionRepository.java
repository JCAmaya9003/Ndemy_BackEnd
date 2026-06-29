package org.example.ndemy_backend.repositories.exams;

import org.example.ndemy_backend.models.exams.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
    List<Option> findByQuestionId(UUID questionId);

    Optional<Option> findByQuestionIdAndIsCorrectTrue(UUID questionId);
}
