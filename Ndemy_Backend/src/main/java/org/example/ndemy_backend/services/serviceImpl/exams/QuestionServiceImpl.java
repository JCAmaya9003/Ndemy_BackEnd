package org.example.ndemy_backend.services.serviceImpl.exams;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.exams.QuestionRequest;
import org.example.ndemy_backend.dto.response.exams.QuestionResponse;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.exams.Exam;
import org.example.ndemy_backend.models.exams.Question;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.repositories.exams.ExamRepository;
import org.example.ndemy_backend.repositories.exams.QuestionRepository;
import org.example.ndemy_backend.repositories.UserRepository;
import org.example.ndemy_backend.services.exams.QuestionService;
import org.example.ndemy_backend.utils.OrderIndexUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;

    @Override
    public QuestionResponse createQuestion(UUID examId, QuestionRequest request, UUID instructorId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        verifyOwnership(exam, instructorId);

        int targetIndex = OrderIndexUtil.resolveOrderIndex(
                request.getOrderIndex(),
                () -> questionRepository.findMaxOrderIndexByExamId(examId).orElse(0) + 1
        );

        if (questionRepository.existsByExamIdAndOrderIndex(examId, targetIndex)) {
            List<Question> toShift = questionRepository
                    .findByExamIdAndOrderIndexGreaterThanEqual(examId, targetIndex);
            toShift.forEach(q -> q.setOrderIndex(q.getOrderIndex() + 1));
            questionRepository.saveAll(toShift);
        }

        Question question = Question.builder()
                .text(request.getText())
                .orderIndex(targetIndex)
                .exam(exam)
                .build();

        questionRepository.save(question);
        reindex(examId);

        return toResponse(questionRepository.findById(question.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found after save")));
    }

    @Override
    public QuestionResponse updateQuestion(UUID questionId, QuestionRequest request, UUID instructorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        verifyOwnership(question.getExam(), instructorId);
        UUID examId = question.getExam().getId();

        question.setText(request.getText());

        if (request.getOrderIndex() != null) {
            int oldIndex = question.getOrderIndex();
            int targetIndex = request.getOrderIndex();

            if (targetIndex < oldIndex) {
                // moves up — shifts middle elements to right
                List<Question> toShift = questionRepository
                        .findByExamIdAndOrderIndexBetween(examId, targetIndex, oldIndex - 1);
                toShift.forEach(q -> q.setOrderIndex(q.getOrderIndex() + 1));
                questionRepository.saveAll(toShift);
            } else if (targetIndex > oldIndex) {
                // moves down — shifts middle elements to left
                List<Question> toShift = questionRepository
                        .findByExamIdAndOrderIndexBetween(examId, oldIndex + 1, targetIndex);
                toShift.forEach(q -> q.setOrderIndex(q.getOrderIndex() - 1));
                questionRepository.saveAll(toShift);
            }

            question.setOrderIndex(targetIndex);
        }

        questionRepository.save(question);
        reindex(examId);

        return toResponse(questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found after save")));
    }

    @Override
    public void deleteQuestion(UUID questionId, UUID instructorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        verifyOwnership(question.getExam(), instructorId);
        UUID examId = question.getExam().getId();

        questionRepository.delete(question);
        reindex(examId);
    }

    private void verifyOwnership(Exam exam, UUID instructorId) {
        boolean isOwner = exam.getCourse().getInstructor().getId().equals(instructorId);
        boolean isAdmin = userRepository.findById(instructorId)
                .map(u -> u.getRole() == Role.ADMIN)
                .orElse(false);
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to modify this question");
        }
    }

    // keeps order index in consecutive order (1,2,3,4,5,...)
    private void reindex(UUID examId) {
        List<Question> questions = questionRepository
                .findByExamIdOrderByOrderIndexAsc(examId);
        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setOrderIndex(i + 1);
        }
        questionRepository.saveAll(questions);
    }

    private QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .orderIndex(question.getOrderIndex())
                .build();
    }
}
