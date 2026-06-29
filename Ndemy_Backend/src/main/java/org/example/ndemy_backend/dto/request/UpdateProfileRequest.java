package org.example.ndemy_backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Size(max = 500, message = "La URL de la foto no puede superar los 500 caracteres")
    private String photoUrl;   // URL de la foto de perfil; null = no actualizar
}
