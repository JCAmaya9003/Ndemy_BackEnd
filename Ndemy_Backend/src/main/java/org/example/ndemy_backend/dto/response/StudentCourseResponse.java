package org.example.ndemy_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCourseResponse {
    private UUID courseId;
    private String courseTitle;
    private String thumbnailUrl;
    private Double progress;
    private Boolean isCompleted;
    private List<LessonResponse> completedLessons;
}
