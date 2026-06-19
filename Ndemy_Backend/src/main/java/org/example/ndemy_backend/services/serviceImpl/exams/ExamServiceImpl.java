package org.example.ndemy_backend.services.serviceImpl.exams;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.ndemy_backend.dto.request.exams.AnswerRequest;
import org.example.ndemy_backend.dto.request.exams.ExamRequest;
import org.example.ndemy_backend.dto.request.exams.ExamSubmitRequest;
import org.example.ndemy_backend.dto.response.exams.*;
import org.example.ndemy_backend.exceptions.*;
import org.example.ndemy_backend.models.Certificate;
import org.example.ndemy_backend.models.Course;
import org.example.ndemy_backend.models.Enrollment;
import org.example.ndemy_backend.models.User;
import org.example.ndemy_backend.models.enums.Role;
import org.example.ndemy_backend.models.exams.Exam;
import org.example.ndemy_backend.models.exams.ExamAttempt;
import org.example.ndemy_backend.models.exams.Option;
import org.example.ndemy_backend.models.exams.Question;
import org.example.ndemy_backend.repositories.*;
import org.example.ndemy_backend.repositories.exams.ExamAttemptRepository;
import org.example.ndemy_backend.repositories.exams.ExamRepository;
import org.example.ndemy_backend.repositories.exams.OptionRepository;
import org.example.ndemy_backend.repositories.exams.QuestionRepository;
import org.example.ndemy_backend.services.exams.ExamService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private static final int MAX_ATTEMPTS = 3;

    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    @Override
    public ExamResponse createExam(UUID courseId, ExamRequest request, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        verifyOwnership(course, instructorId);

        if (examRepository.existsByCourseId(courseId)) {
            throw new ExamAlreadyExistsException("Course already has an exam");
        }

        Exam exam = Exam.builder()
                .course(course)
                .passingScore(request.getPassingScore())
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .build();

        return toExamResponse(examRepository.save(exam), true);
    }

    @Override
    public ExamResponse getExam(UUID courseId, UUID requesterId, boolean isPrivileged) {

        if(courseRepository.findById(courseId).isEmpty()) {
            throw new ResourceNotFoundException("Course not found");
        }

        Exam exam = examRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!isPrivileged) {
            // verify enrollment
            Enrollment enrollment = enrollmentRepository
                    .findByStudentIdAndCourseIdAndIsActiveTrue(requesterId, courseId)
                    .orElseThrow(() -> new NotEnrolledException("You are not enrolled in this course"));

            // verify 100% progress
            double progress = calculateProgress(enrollment.getId(), courseId);
            if (progress < 100.0) {
                throw new ExamNotUnlockedException("Complete all lessons before taking the exam");
            }
        }

        return toExamResponse(exam, isPrivileged);
    }

    @Override
    public ExamResponse updateExam(UUID courseId, ExamRequest request, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        verifyOwnership(course, instructorId);

        Exam exam = examRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        exam.setPassingScore(request.getPassingScore());
        exam.setTimeLimitMinutes(request.getTimeLimitMinutes());

        return toExamResponse(examRepository.save(exam), true);
    }

    @Override
    public void deleteExam(UUID courseId, UUID requesterId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!course.getInstructor().getId().equals(requesterId)
                && requester.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You are not allowed to delete this exam");
        }

        Exam exam = examRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        examRepository.delete(exam);
    }

    @Override
    @Transactional
    public ExamResultResponse submitExam(UUID courseId, UUID studentId, ExamSubmitRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Exam exam = examRepository.findByCourseId(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        // verify enrollment
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndCourseIdAndIsActiveTrue(studentId, courseId)
                .orElseThrow(() -> new NotEnrolledException("You are not enrolled in this course"));

        // verify 100% progress
        double progress = calculateProgress(enrollment.getId(), courseId);
        if (progress < 100.0) {
            throw new ExamNotUnlockedException("Complete all lessons before taking the exam");
        }

        // get or create attempt
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        ExamAttempt attempt = examAttemptRepository
                .findByStudentIdAndExamId(studentId, exam.getId())
                .orElse(ExamAttempt.builder()
                        .exam(exam)
                        .student(student)
                        .attempts(0)
                        .passed(false)
                        .build());

        // verify attempts left
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            throw new ExamAttemptsExceededException("You have exceeded the maximum attempts for this exam");
        }

        // calculate score
        List<Question> questions = questionRepository.findByExamIdOrderByOrderIndexAsc(exam.getId());
        int correct = 0;

        List<QuestionResultResponse> questionResults = new ArrayList<>();

        for (Question question : questions) {
            Option correctOption = optionRepository
                    .findByQuestionIdAndIsCorrectTrue(question.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Correct option not found"));

            // find student answer for this question
            UUID selectedOptionId = request.getAnswers().stream()
                    .filter(a -> a.getQuestionId().equals(question.getId()))
                    .map(AnswerRequest::getOptionId)
                    .findFirst()
                    .orElse(null);

            boolean answeredCorrectly = correctOption.getId().equals(selectedOptionId);
            if (answeredCorrectly) correct++;

            // build options result
            List<Option> options = optionRepository.findByQuestionId(question.getId());
            List<OptionResultResponse> optionResults = options.stream()
                    .map(o -> OptionResultResponse.builder()
                            .id(o.getId())
                            .text(o.getText())
                            .isCorrect(o.getIsCorrect())
                            .wasSelected(o.getId().equals(selectedOptionId))
                            .build())
                    .toList();

            questionResults.add(QuestionResultResponse.builder()
                    .id(question.getId())
                    .text(question.getText())
                    .answeredCorrectly(answeredCorrectly)
                    .options(optionResults)
                    .build());
        }

        // calculate score percentage
        BigDecimal score = questions.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf((correct * 100.0) / questions.size())
                .setScale(2, RoundingMode.HALF_UP);

        boolean passed = score.compareTo(BigDecimal.valueOf(exam.getPassingScore())) >= 0;

        // update attempt
        attempt.setScore(score);
        attempt.setPassed(passed);
        attempt.setAttempts(attempt.getAttempts() + 1);
        examAttemptRepository.save(attempt);

        boolean courseReset = false;

        if (passed) {
            // generate certificate
            generateCertificate(student, course);
        } else if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            // reset course
            resetCourse(enrollment.getId(), exam.getId(), studentId);
            courseReset = true;
        }

        int attemptsRemaining = courseReset ? MAX_ATTEMPTS :
                MAX_ATTEMPTS - attempt.getAttempts();

        return ExamResultResponse.builder()
                .score(score)
                .passed(passed)
                .attemptsUsed(attempt.getAttempts())
                .attemptsRemaining(attemptsRemaining)
                .courseReset(courseReset)
                .questions(questionResults)
                .build();
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new UnauthorizedException("You are not allowed to modify this exam");
        }
    }

    private double calculateProgress(UUID enrollmentId, UUID courseId) {
        int completed = lessonProgressRepository.countByEnrollmentId(enrollmentId);
        int total = lessonRepository.countByModuleCourseId(courseId);
        if (total == 0) return 0.0;
        return (completed * 100.0) / total;
    }

    private void generateCertificate(User student, Course course) {
        if (certificateRepository.existsByStudentIdAndCourseId(
                student.getId(), course.getId())) {
            return;
        }

        Certificate certificate = Certificate.builder()
                .course(course)
                .student(student)
                .certificateCode(UUID.randomUUID().toString())
                .build();

        certificateRepository.save(certificate);
    }

    private void resetCourse(UUID enrollmentId, UUID examId, UUID studentId) {
        // delete lesson progress
        lessonProgressRepository.deleteByEnrollmentId(enrollmentId);

        // reset exam attempt
        examAttemptRepository.findByStudentIdAndExamId(studentId, examId)
                .ifPresent(attempt -> {
                    attempt.setAttempts(0);
                    attempt.setScore(null);
                    attempt.setPassed(false);
                    examAttemptRepository.save(attempt);
                });

    }

    private ExamResponse toExamResponse(Exam exam, boolean showCorrectAnswers) {
        List<Question> questions = questionRepository
                .findByExamIdOrderByOrderIndexAsc(exam.getId());

        // shuffle questions for students to prevent copying
        if (!showCorrectAnswers) {
            questions = new ArrayList<>(questions);
            Collections.shuffle(questions);
        }

        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::toQuestionResponse)
                .toList();

        return ExamResponse.builder()
                .id(exam.getId())
                .passingScore(exam.getPassingScore())
                .timeLimitMinutes(exam.getTimeLimitMinutes())
                .questions(questionResponses)
                .build();
    }

    private QuestionResponse toQuestionResponse(Question question) {
        List<Option> options = optionRepository.findByQuestionId(question.getId());

        List<OptionResponse> optionResponses = options.stream()
                .map(o -> OptionResponse.builder()
                        .id(o.getId())
                        .text(o.getText())
                        .build())
                .toList();

        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .orderIndex(question.getOrderIndex())
                .options(optionResponses)
                .build();
    }
}
