package org.example.ndemy_backend.dto.request.exams;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotNull(message = "Option ID is required")
    private UUID optionId;
}