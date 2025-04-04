package com.projects.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest
{
    @NotBlank(message = "Email est requis")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Mot de passe est requis")
    private String password;
}
