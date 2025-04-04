package com.projects.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RegisterRequest
{

    @NotBlank(message = "Le nom d'utilisateur est requis!")
    @Size(min = 6, max = 20, message = "Le nom d'utilisateur doit contenir entre 6 et 20 caractères")
    private String username;

    @NotBlank(message = "L'email est requis!")
    @Email(message = "Format d'email invalide!")
    private String email;

    @NotBlank(message = "Le mot de passe est requis!")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial!"
    )
    private String password;
}
