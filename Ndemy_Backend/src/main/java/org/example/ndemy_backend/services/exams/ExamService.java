package org.example.ndemy_backend.services.exams;

import org.example.ndemy_backend.dto.request.exams.ExamRequest;
import org.example.ndemy_backend.dto.request.exams.ExamSubmitRequest;
import org.example.ndemy_backend.dto.response.exams.ExamResponse;
import org.example.ndemy_backend.dto.response.exams.ExamResultResponse;

import java.util.UUID;

public interface ExamService {
    ExamResponse createExam(UUID courseId, ExamRequest request,UUID instructorId);
    ExamResponse getExam(UUID courseId, UUID requesterId, boolean isPrivileged);
    ExamResponse updateExam(UUID courseId, ExamRequest request, UUID instructorId);
    void deleteExam(UUID courseId, UUID requesterId);
    ExamResultResponse submitExam(UUID courseId, UUID studentId, ExamSubmitRequest request);
}
