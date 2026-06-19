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

import static org.example.ndemy_backend.utils.OrderIndexUtil.resolveOrderIndex;

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

        // in request, order index is optional, if you don't send it, it will be assigned automatically at the end
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

        moduleRepository.save(module);
        reindex(courseId);

        return toResponse(moduleRepository.findById(module.getId())
                .orElseThrow(() ->  new ResourceNotFoundException("Module not found")));
    }

    @Override
    public ModuleResponse updateModule(UUID moduleId, ModuleRequest request, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        verifyOwnership(module.getCourse(), instructorId);
        UUID courseId = module.getCourse().getId();

        module.setTitle(request.getTitle());

        if (request.getOrderIndex() != null) {
            int oldIndex = module.getOrderIndex();
            int targetIndex = request.getOrderIndex();

            if (targetIndex < oldIndex) {
                // moves up — shifts middle elements to right
                List<Module> toShift = moduleRepository
                        .findByCourseIdAndOrderIndexBetween(courseId, targetIndex, oldIndex - 1);
                toShift.forEach(m -> m.setOrderIndex(m.getOrderIndex() + 1));
                moduleRepository.saveAll(toShift);
            } else if (targetIndex > oldIndex) {
                // moves down - shifts middle elements to left
                List<Module> toShift = moduleRepository
                        .findByCourseIdAndOrderIndexBetween(courseId, oldIndex + 1, targetIndex);
                toShift.forEach(m -> m.setOrderIndex(m.getOrderIndex() - 1));
                moduleRepository.saveAll(toShift);
            }

            module.setOrderIndex(targetIndex);
        }

        moduleRepository.save(module);
        reindex(courseId);

        return toResponse(moduleRepository.findById(moduleId)
                .orElseThrow(() ->  new ResourceNotFoundException("Module not found")));
    }

    @Override
    public void deleteModule(UUID moduleId, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        verifyOwnership(module.getCourse(), instructorId);
        UUID courseId = module.getCourse().getId();

        moduleRepository.delete(module);
        reindex(courseId);
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if(!course.getInstructor().getId().equals(instructorId)){
            throw new UnauthorizedException("You are not allowed to modify this course");
        }
    }

    private ModuleResponse toResponse(Module module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .orderIndex(module.getOrderIndex())
                .build();
    }

    // helper to keep order index in consecutive order (1,2,3,4,5,...)
    private void reindex(UUID courseId) {
        List<Module> modules = moduleRepository
                .findByCourseIdOrderByOrderIndexAsc(courseId);
        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).setOrderIndex(i + 1);
        }
        moduleRepository.saveAll(modules);
    }
}
