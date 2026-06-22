package org.example.ndemy_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CourseNotReadyException extends RuntimeException {
    public CourseNotReadyException(String message) {
        super(message);
    }
}