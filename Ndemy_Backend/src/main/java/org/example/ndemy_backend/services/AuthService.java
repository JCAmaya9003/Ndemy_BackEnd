package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.LoginRequest;
import org.example.ndemy_backend.dto.request.RegisterRequest;
import org.example.ndemy_backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(String refreshToken);
}