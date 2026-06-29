package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ndemy_backend.models.enums.ContentType;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonResponse {
    private UUID id;
    private String title;
    private ContentType contentType;
    private Integer orderIndex;
    private String contentUrl;
}
