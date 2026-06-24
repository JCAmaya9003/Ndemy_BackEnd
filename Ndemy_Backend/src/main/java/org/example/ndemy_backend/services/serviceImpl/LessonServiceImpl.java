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

import static org.example.ndemy_backend.utils.OrderIndexUtil.resolveOrderIndex;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonResponse createLesson(UUID moduleId, LessonRequest request, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        verifyOwnership(module.getCourse(), instructorId);

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

        lessonRepository.save(lesson);
        reindex(moduleId);
        return toResponse(lessonRepository.findById(lesson.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found after save")));
    }

    public LessonResponse updateLesson(UUID lessonId, LessonRequest request, UUID instructorId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        verifyOwnership(lesson.getModule().getCourse(), instructorId);
        UUID moduleId = lesson.getModule().getId();

        lesson.setTitle(request.getTitle());
        lesson.setContentType(request.getContentType());
        lesson.setContentUrl(request.getContentUrl());

        if(request.getOrderIndex() != null) {
            int oldIndex = lesson.getOrderIndex();
            int targetIndex = request.getOrderIndex();

            if (targetIndex < oldIndex) {
                // moves up — shifts middle elements to right
                List<Lesson> toShift = lessonRepository
                        .findByModuleIdAndOrderIndexBetween(moduleId, targetIndex, oldIndex - 1);
                toShift.forEach(l -> l.setOrderIndex(l.getOrderIndex() + 1));
                lessonRepository.saveAll(toShift);
            } else if (targetIndex > oldIndex) {
                // moves down — shifts middle elements to left
                List<Lesson> toShift = lessonRepository
                        .findByModuleIdAndOrderIndexBetween(moduleId, oldIndex + 1, targetIndex);
                toShift.forEach(l -> l.setOrderIndex(l.getOrderIndex() - 1));
                lessonRepository.saveAll(toShift);
            }

            lesson.setOrderIndex(targetIndex);
        }

        lessonRepository.save(lesson);
        reindex(moduleId);
        return toResponse(lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found after save")));
    }

    public void deleteLesson(UUID lessonId, UUID instructorId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        verifyOwnership(lesson.getModule().getCourse(), instructorId);
        UUID moduleId = lesson.getModule().getId();

        lessonRepository.delete(lesson);
        reindex(moduleId);
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("You are not allowed to modify this lesson");
        }
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

    // helper to keep order index in consecutive order (1,2,3,4,5,...)
    private void reindex(UUID moduleId) {
        List<Lesson> lessons = lessonRepository
                .findByModuleIdOrderByOrderIndexAsc(moduleId);
        for (int i = 0; i < lessons.size(); i++) {
            lessons.get(i).setOrderIndex(i + 1);
        }
        lessonRepository.saveAll(lessons);
    }

    @Override
    public UUID getCourseIdByLessonId(UUID lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        return lesson.getModule().getCourse().getId();
    }

    @Override
    public boolean isInstructorOfCourse(UUID userId, UUID courseId) {
        return lessonRepository.existsByModuleCourseIdAndInstructorId(courseId, userId);
    }

    @Override
    public LessonResponse getLessonById(UUID lessonId, UUID userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        return toResponse(lesson);
    }


}
