package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRequest {
    @NotBlank(message = "Module must have a title")
    @Size(max = 200, message = "Module title exceeds character limit, max: 200 characters")
    private String title;

    @PositiveOrZero(message = "Module order index must be 0 or higher")
    private Integer orderIndex;
}
