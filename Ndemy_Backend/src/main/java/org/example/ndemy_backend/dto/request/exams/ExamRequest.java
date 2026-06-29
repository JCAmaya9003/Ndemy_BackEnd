package org.example.ndemy_backend.dto.request.exams;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamRequest {
    @NotNull(message = "Exam must have a passing score")
    @Min(value = 60, message = "Exam minimum passing score must be at least 60")
    @Max(value = 100, message = "Exam maximum passing score is 100")
    private Integer passingScore;

    @Min(value = 5, message = "Exam minimum time must be 5 minutes")
    @Max(value = 300, message = "Exam time limit cannot exceed 300 minutes (5 hours)")
    private Integer timeLimitMinutes;
}
