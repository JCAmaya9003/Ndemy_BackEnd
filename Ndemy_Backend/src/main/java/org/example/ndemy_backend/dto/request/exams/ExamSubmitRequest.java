package org.example.ndemy_backend.dto.request.exams;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmitRequest {
    @NotEmpty(message = "To submit the exam, you must have answered the questions")
    @Valid
    private List<AnswerRequest> answers;
}