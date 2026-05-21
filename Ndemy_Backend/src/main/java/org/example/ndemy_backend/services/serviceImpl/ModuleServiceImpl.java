package org.example.ndemy_backend.services.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.ModuleRequest;
import org.example.ndemy_backend.dto.response.ModuleResponse;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Module;
import org.example.ndemy_backend.repositories.CourseRepository;
import org.example.ndemy_backend.repositories.ModuleRepository;
import org.example.ndemy_backend.services.ModuleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Override
    public ModuleResponse createModule(UUID courseId, ModuleRequest request, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        verifyOwnership(course, instructorId);

        // in request, oder index is optional, if you don't send it, it will be assigned automatically at the end
        int targetIndex = resolveOrderIndex(
                request.getOrderIndex(),
                () -> moduleRepository.findMaxOrderIndexByCourseId(courseId).orElse(0) + 1
        );

        // in request if you send an order index that already exists, the modules will be shifted one space to the right
        // if the user wants to add a module between others, this function allows it
        if (moduleRepository.existsByCourseIdAndOrderIndex(courseId, targetIndex)) {
            List<Module> toShift = moduleRepository
                    .findByCourseIdAndOrderIndexGreaterThanEqual(courseId, targetIndex);
            toShift.forEach(m -> m.setOrderIndex(m.getOrderIndex() + 1));

            moduleRepository.saveAll(toShift);
        }
        
        Module module = Module
                .builder()
                .title(request.getTitle())
                .orderIndex(targetIndex)
                .course(course)
                .build();

        return toResponse(moduleRepository.save(module));
    }

    @Override
    public ModuleResponse updateModule(UUID moduleId, ModuleRequest request, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        verifyOwnership(module.getCourse(), instructorId);

        module.setTitle(request.getTitle());

        if (request.getOrderIndex() != null) {
            int targetIndex = request.getOrderIndex();
            UUID courseId = module.getCourse().getId();

            if(moduleRepository.existsByCourseIdAndOrderIndex(courseId, targetIndex)) {
                List<Module> toShift = moduleRepository
                        .findByCourseIdAndOrderIndexGreaterThanEqual(courseId, targetIndex);
                toShift.forEach(m -> {
                    // it doesn't shift the updated module
                    if(!m.getId().equals(moduleId)) {
                        m.setOrderIndex(m.getOrderIndex() + 1);
                    }
                });
                moduleRepository.saveAll(toShift);
            }

            module.setOrderIndex(targetIndex);
        }

        return toResponse(moduleRepository.save(module));
    }

    @Override
    public void deleteModule(UUID moduleId, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        verifyOwnership(module.getCourse(), instructorId);

        moduleRepository.delete(module);
    }

    @Override
    public List<ModuleResponse> getModulesByCourse(UUID courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if(!course.getInstructor().getId().equals(instructorId)){
            throw new UnauthorizedException("You are not allowed to modify this course");
        }
    }

    private int resolveOrderIndex(Integer requested, Supplier<Integer> fallback) {
        return requested != null ? requested : fallback.get();
    }

    private ModuleResponse toResponse(Module module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .orderIndex(module.getOrderIndex())
                .build();
    }
}
