package org.example.ndemy_backend.services.exams;

import org.example.ndemy_backend.dto.request.exams.QuestionRequest;
import org.example.ndemy_backend.dto.response.exams.QuestionResponse;

import java.util.UUID;

public interface QuestionService {
    QuestionResponse createQuestion(UUID examId, QuestionRequest request, UUID instructorId);
    QuestionResponse updateQuestion(UUID questionId, QuestionRequest request, UUID instructorId);
    void deleteQuestion(UUID questionId, UUID instructorId);
}
