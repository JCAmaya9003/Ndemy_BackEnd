package org.example.ndemy_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExamAlreadyExistsException extends RuntimeException {
    public ExamAlreadyExistsException(String message) {
        super(message);
    }
}
