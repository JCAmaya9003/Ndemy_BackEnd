package org.example.ndemy_backend.services.exams;

import org.example.ndemy_backend.dto.request.exams.OptionRequest;
import org.example.ndemy_backend.dto.response.exams.OptionResponse;

import java.util.UUID;

public interface OptionService {
    OptionResponse createOption(UUID questionId, OptionRequest request, UUID instructorId);
    OptionResponse updateOption(UUID optionId, OptionRequest request, UUID instructorId);
    void deleteOption(UUID optionId, UUID instructorId);
}
