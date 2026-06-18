package org.example.ndemy_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCouponCodeException extends RuntimeException {
    public DuplicateCouponCodeException(String message) { super(message); }
}
