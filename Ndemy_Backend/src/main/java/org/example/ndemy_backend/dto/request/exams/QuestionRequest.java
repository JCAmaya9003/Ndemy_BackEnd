package org.example.ndemy_backend.dto.request.exams;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {
    @NotBlank(message = "Question must have a title")
    @Size(max = 500, message = "Question title exceeds character limit, max: 500 characters")
    private String title;

    @PositiveOrZero(message = "Question order index must be 0 or higher")
    private Integer orderIndex;
}
