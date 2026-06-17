package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.ndemy_backend.models.enums.Role;


@Data
public class UpdateUserRequest {

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Email(message = "El email no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    private String password;

    private Role role;
}
