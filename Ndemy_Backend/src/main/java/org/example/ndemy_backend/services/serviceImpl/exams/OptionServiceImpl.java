package org.example.ndemy_backend.services.serviceImpl.exams;

import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.exams.OptionRequest;
import org.example.ndemy_backend.dto.response.exams.OptionResponse;
import org.example.ndemy_backend.exceptions.CorrectOptionDeletionException;
import org.example.ndemy_backend.exceptions.ResourceNotFoundException;
import org.example.ndemy_backend.exceptions.UnauthorizedException;
import org.example.ndemy_backend.models.exams.Option;
import org.example.ndemy_backend.models.exams.Question;
import org.example.ndemy_backend.repositories.exams.OptionRepository;
import org.example.ndemy_backend.repositories.exams.QuestionRepository;
import org.example.ndemy_backend.services.exams.OptionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;

    @Override
    public OptionResponse createOption(UUID questionId, OptionRequest request, UUID instructorId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        verifyOwnership(question, instructorId);

        // only one correct option allowed per question
        if (Boolean.TRUE.equals(request.getIsCorrect())) {
            unsetPreviousCorrectOption(questionId);
        }

        Option option = Option.builder()
                .text(request.getText())
                .isCorrect(request.getIsCorrect())
                .question(question)
                .build();

        return toResponse(optionRepository.save(option));
    }

    @Override
    public OptionResponse updateOption(UUID optionId, OptionRequest request, UUID instructorId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));

        verifyOwnership(option.getQuestion(), instructorId);

        // only one correct option allowed per question
        if (Boolean.TRUE.equals(request.getIsCorrect())) {
            unsetPreviousCorrectOption(option.getQuestion().getId());
        }

        option.setText(request.getText());
        option.setIsCorrect(request.getIsCorrect());

        return toResponse(optionRepository.save(option));
    }

    @Override
    public void deleteOption(UUID optionId, UUID instructorId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));

        verifyOwnership(option.getQuestion(), instructorId);

        // prevent deleting the correct option without reassigning first
        if (Boolean.TRUE.equals(option.getIsCorrect())) {
            throw new CorrectOptionDeletionException(
                    "You must mark another option as correct before deleting this one");
        }

        optionRepository.delete(option);
    }

    private void verifyOwnership(Question question, UUID instructorId) {
        if (!question.getExam().getCourse().getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("You are not allowed to modify this option");
        }
    }

    // ensures only one option is marked as correct per question
    private void unsetPreviousCorrectOption(UUID questionId) {
        optionRepository.findByQuestionIdAndIsCorrectTrue(questionId)
                .ifPresent(previous -> {
                    previous.setIsCorrect(false);
                    optionRepository.save(previous);
                });
    }

    private OptionResponse toResponse(Option option) {
        return OptionResponse.builder()
                .id(option.getId())
                .text(option.getText())
                .isCorrect(option.getIsCorrect())
                .build();
    }
}