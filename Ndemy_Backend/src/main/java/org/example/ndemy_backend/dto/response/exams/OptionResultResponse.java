package org.example.ndemy_backend.dto.response.exams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResultResponse {
    private UUID id;
    private String text;
    private Boolean isCorrect;
    private Boolean wasSelected;
}
