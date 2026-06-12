package org.example.ndemy_backend.dto.response.exams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {
    private BigDecimal score;
    private Boolean passed;
    private Integer attemptsUsed;
    private Integer attemptsRemaining;
    private Boolean courseReset;
    private List<QuestionResultResponse> questions;
}
