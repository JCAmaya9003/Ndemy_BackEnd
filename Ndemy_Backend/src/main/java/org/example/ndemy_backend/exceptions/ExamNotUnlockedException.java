package org.example.ndemy_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ExamNotUnlockedException extends RuntimeException {
    public ExamNotUnlockedException(String message) {
        super(message);
    }
}
