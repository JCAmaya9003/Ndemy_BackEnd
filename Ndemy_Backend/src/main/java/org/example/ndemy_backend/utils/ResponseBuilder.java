package org.example.ndemy_backend.utils;

import org.example.ndemy_backend.dto.response.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

public class ResponseBuilder {
    public static ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity.status(status)
                .body(GeneralResponse.builder()
                        .uri(uri)
                        .message(message)
                        .status(status.value())
                        .time(LocalDateTime.now())
                        .data(data)
                        .build());
    }
}