package org.example.ndemy_backend.exceptions;

import org.example.ndemy_backend.dto.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AlreadyReviewedException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyReviewed(AlreadyReviewedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AlreadyEnrolledException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyEnrolled(AlreadyEnrolledException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CouponExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleCouponExpired(CouponExpiredException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CouponExhaustedException.class)
    public ResponseEntity<ApiErrorResponse> handleCouponExhausted(CouponExhaustedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CouponAlreadyUsedException.class)
    public ResponseEntity<ApiErrorResponse> handleCouponAlreadyUsed(CouponAlreadyUsedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CouponInactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleCouponInactive(CouponInactiveException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CourseNotPublishedException.class)
    public ResponseEntity<ApiErrorResponse> handleCourseNotPublished(CourseNotPublishedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NotEnrolledException.class)
    public ResponseEntity<ApiErrorResponse> handleNotEnrolled(NotEnrolledException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(CertificateAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCertificateAlreadyExists(CertificateAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(LessonAlreadyCompletedException.class)
    public ResponseEntity<ApiErrorResponse> handleLessonAlreadyCompleted(LessonAlreadyCompletedException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, Object message) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity
                .status(status)
                .body(ApiErrorResponse.builder()
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .uri(uri)
                        .build());
    }
}