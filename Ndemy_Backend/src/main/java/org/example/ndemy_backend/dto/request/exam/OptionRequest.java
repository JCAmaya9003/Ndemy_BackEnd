package org.example.ndemy_backend.dto.request.exam;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequest {
    @NotBlank(message = "Option must have a text")
    @Size(max = 300, message = "Option text exceeds character limit, max: 300 characters")
    private String text;

    @NotNull(message = "You must indicate if this option is correct (true or false)")
    private Boolean isCorrect;
}
