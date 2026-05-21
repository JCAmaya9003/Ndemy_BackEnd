package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.LessonRequest;
import org.example.ndemy_backend.dto.response.LessonResponse;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    LessonResponse createLesson(UUID moduleId, LessonRequest request, UUID instructorId);
    LessonResponse updateLesson(UUID lessonId, LessonRequest request, UUID instructorId);
    void deleteLesson(UUID lessonId, UUID instructorId);
    List<LessonResponse> getLessonsByModule(UUID moduleId);
}
