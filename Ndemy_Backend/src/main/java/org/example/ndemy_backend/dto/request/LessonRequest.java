package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ndemy_backend.models.enums.ContentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonRequest {
    @NotBlank(message = "Lesson must have a title")
    @Size(max = 200, message = "Lesson title exceeds character limit, max: 200 characters")
    private String title;

    @NotNull(message = "Lesson content type is required")
    private ContentType contentType;

    @Size(max = 500,message ="Lesson content url exceeds character limit, max: 500 characters")
    private String contentUrl;

    @PositiveOrZero(message = "Lesson order index must be 0 or higher")
    private Integer orderIndex;
}
