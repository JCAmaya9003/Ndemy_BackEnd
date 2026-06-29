package org.example.ndemy_backend.dto.response.exams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private UUID id;
    private Integer passingScore;
    private Integer timeLimitMinutes;
    private List<QuestionResponse> questions;
}
