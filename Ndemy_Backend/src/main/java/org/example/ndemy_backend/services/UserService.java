package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.UpdateUserRequest;
import org.example.ndemy_backend.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    void deactivateUser(UUID id);
    void unlockUser(UUID id);
}
