package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.LessonRequest;
import org.example.ndemy_backend.dto.response.LessonResponse;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Lesson;
import org.example.ndemy_backend.models.Module;
import org.example.ndemy_backend.repositories.LessonRepository;
import org.example.ndemy_backend.repositories.ModuleRepository;
import org.example.ndemy_backend.services.LessonService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonResponse createLesson(UUID moduleId, LessonRequest request, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId).
                orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        verifyOwnership(module.getCourse(), instructorId);

        // in request, oder index is optional, if you don't send it, it will be assigned automatically at the end
        int targetIndex = resolveOrderIndex(
                request.getOrderIndex(),
                () -> lessonRepository.findMaxOrderIndexByModuleId(moduleId).orElse(0) + 1
        );

        // in request if you send an order index that already exists, the lessons will be shifted one space to the right
        // if the user wants to add a lesson between others, this function allows it
        if(lessonRepository.existsByModuleIdAndOrderIndex(moduleId, targetIndex)) {
            List<Lesson> toShift = lessonRepository
                    .findByModuleIdAndOrderIndexGreaterThanEqual(moduleId, targetIndex);
            toShift.forEach(l -> l.setOrderIndex(l.getOrderIndex() + 1));
            
            lessonRepository.saveAll(toShift);
        }

        Lesson lesson = Lesson
                .builder()
                .title(request.getTitle())
                .contentType(request.getContentType())
                .contentUrl(request.getContentUrl())
                .orderIndex(targetIndex)
                .module(module)
                .build();

        return toResponse(lessonRepository.save(lesson));
    }

    public LessonResponse updateLesson(UUID lessonId, LessonRequest request, UUID instructorId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson not found")
        );

        verifyOwnership(lesson.getModule().getCourse(), instructorId);

        lesson.setTitle(request.getTitle());
        lesson.setContentType(request.getContentType());
        lesson.setContentUrl(request.getContentUrl());

        if(request.getOrderIndex() != null) {
            int targetIndex = request.getOrderIndex();
            UUID moduleId = lesson.getModule().getId();

            if(lessonRepository.existsByModuleIdAndOrderIndex(moduleId, targetIndex)) {
                List<Lesson> toShift =  lessonRepository
                        .findByModuleIdAndOrderIndexGreaterThanEqual(moduleId, targetIndex);
                // it doesn't shift the updated lesson
                toShift.forEach(l -> {
                    if(!l.getId().equals(lesson.getId())) {
                        l.setOrderIndex(l.getOrderIndex() + 1);
                    }
                });
                lessonRepository.saveAll(toShift);
            }

            lesson.setOrderIndex(targetIndex);
        }

        return toResponse(lessonRepository.save(lesson));
    }

    public void deleteLesson(UUID lessonId, UUID instructorId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson not found")
        );

        verifyOwnership(lesson.getModule().getCourse(), instructorId);

        lessonRepository.delete(lesson);
    }

    public List<LessonResponse> getLessonsByModule(UUID moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("You are not allowed to modify this lesson");
        }
    }

    private int resolveOrderIndex(Integer requested, Supplier<Integer> fallback) {
        return requested != null ? requested : fallback.get();
    }

    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .contentType(lesson.getContentType())
                .orderIndex(lesson.getOrderIndex())
                .contentUrl(lesson.getContentUrl())
                .build();
    }
}
