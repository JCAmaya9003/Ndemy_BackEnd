package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.LessonRequest;
import org.example.ndemy_backend.dto.response.LessonResponse;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    LessonResponse createLesson(UUID moduleId, LessonRequest request, UUID instructorId);
    LessonResponse updateLesson(UUID lessonId, LessonRequest request, UUID instructorId);
    void deleteLesson(UUID lessonId, UUID instructorId);
    // Retorna el courseId al que pertenece una lección (para verificar acceso)
    UUID getCourseIdByLessonId(UUID lessonId);

    // Retorna true si el usuario es el instructor dueño del curso
    boolean isInstructorOfCourse(UUID userId, UUID courseId);

    // Retorna el contenido de una lección (acceso ya validado en el controller)
    LessonResponse getLessonById(UUID lessonId, UUID userId);
}
