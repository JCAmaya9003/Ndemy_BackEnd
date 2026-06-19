package org.example.ndemy_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CorrectOptionDeletionException extends RuntimeException {
    public CorrectOptionDeletionException(String message) {
        super(message);
    }
}