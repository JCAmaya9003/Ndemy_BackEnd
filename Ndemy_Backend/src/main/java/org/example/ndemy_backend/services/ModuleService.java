package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.ModuleRequest;
import org.example.ndemy_backend.dto.response.ModuleResponse;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    ModuleResponse createModule(UUID courseId, ModuleRequest request, UUID instructorId);
    ModuleResponse updateModule(UUID moduleId, ModuleRequest request, UUID instructorId);
    void deleteModule(UUID moduleId, UUID instructorId);
}
