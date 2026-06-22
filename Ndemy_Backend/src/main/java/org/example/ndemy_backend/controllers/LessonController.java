package org.example.ndemy_backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.LessonRequest;
import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.services.EnrollmentService;
import org.example.ndemy_backend.services.LessonProgressService;
import org.example.ndemy_backend.services.LessonService;
import org.example.ndemy_backend.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final LessonProgressService lessonProgressService;
    private final EnrollmentService enrollmentService;
    // orderIndex is optional in lesson requests
    // if not provided, it will be assigned automatically at the end
    // if the index already exists, existing lessons will be shifted to the right
    // this allows inserting lessons between existing ones

    // Lessons can only be created by instructors in modules that belong to their own courses
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<GeneralResponse> createLesson(
            @PathVariable UUID moduleId,
            @RequestBody @Valid LessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Lesson created successfully",
                HttpStatus.CREATED,
                lessonService.createLesson(moduleId, request, currentUser.getId())
        );
    }

    /**
     * Retorna el contenido de una lección.
     * Acceso permitido si:
     *   - El usuario es ADMIN, o
     *   - El usuario es el instructor dueño del curso, o
     *   - El usuario es estudiante con inscripción activa en el curso.
     */
    @GetMapping("/lessons/{id}")
    public ResponseEntity<GeneralResponse> getLessonById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {

        // El servicio resuelve a qué curso pertenece esta lección
        UUID courseId = lessonService.getCourseIdByLessonId(id);

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isInstructor = lessonService.isInstructorOfCourse(currentUser.getId(), courseId);

        if (!isAdmin && !isInstructor) {
            // Para cualquier otro rol (STUDENT, etc.) se requiere inscripción activa
            if (!enrollmentService.hasUserPaidCourse(currentUser.getId(), courseId)) {
                throw new AccessDeniedException(
                        "Debes estar inscrito para acceder a este contenido"
                );
            }
        }

        return ResponseBuilder.buildResponse(
                "Lesson retrieved successfully",
                HttpStatus.OK,
                lessonService.getLessonById(id, currentUser.getId())
        );
    }

    // Lessons can only be edited by the owner instructor
    @PutMapping("/lessons/{id}")
    public ResponseEntity<GeneralResponse> updateLesson(
            @PathVariable UUID id,
            @RequestBody @Valid LessonRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Lesson updated successfully",
                HttpStatus.OK,
                lessonService.updateLesson(id, request, currentUser.getId())
        );
    }

    // Lessons can only be deleted by the owner instructor
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<GeneralResponse> deleteLesson(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        lessonService.deleteLesson(id, currentUser.getId());
        return ResponseBuilder.buildResponse(
                "Lesson deleted successfully",
                HttpStatus.OK,
                null
        );
    }

    @PostMapping("/lessons/{id}/complete")
    public ResponseEntity<GeneralResponse> completeLesson(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseBuilder.buildResponse(
                "Lesson completed successfully",
                HttpStatus.OK,
                lessonProgressService.completeLesson(currentUser.getId(), id)
        );
    }
}
