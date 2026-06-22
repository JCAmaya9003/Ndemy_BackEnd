package org.example.ndemy_backend.services;

import org.example.ndemy_backend.dto.request.ChangePasswordRequest;
import org.example.ndemy_backend.dto.request.UpdateProfileRequest;
import org.example.ndemy_backend.dto.request.UpdateUserRequest;
import org.example.ndemy_backend.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //-- Para admins o logica
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    void deactivateUser(UUID id);
    void unlockUser(UUID id);

    // ── Perfil propio (cualquier usuario autenticado) ─────────────────────

    /**
     * Retorna el UserResponse del usuario autenticado.
     * El email se extrae desde el SecurityContext en el controlador.
     */
    UserResponse getMyProfile(String email);

    /**
     * Actualiza nombre y/o foto de perfil del usuario autenticado.
     */
    UserResponse updateMyProfile(String email, UpdateProfileRequest request);

    /**
     * Cambia la contraseña del usuario autenticado.
     * Verifica la contraseña actual con BCrypt antes de guardar la nueva.
     */
    void changePassword(String email, ChangePasswordRequest request);
}
