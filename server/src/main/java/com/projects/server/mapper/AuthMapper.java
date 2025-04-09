package com.projects.server.mapper;

import com.projects.server.domain.entities.User;
import com.projects.server.domain.enums.RoleType;
import com.projects.server.dto.request.RegisterRequest;
import com.projects.server.dto.response.AuthResponse;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    // Convertir RegisterRequest en User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true) // Géré manuellement via @AfterMapping
    @Mapping(target = "profileImageUrl", ignore = true)
    @Mapping(target = "password", ignore = true) // Le mot de passe doit être encodé séparément
    User mapToUser(RegisterRequest request);

    // Configurer des valeurs après le mapping
    @AfterMapping
    default void setDefaultRoles(@MappingTarget User user) {
        // Ajouter le rôle USER par défaut
        user.setRoles(Set.of(RoleType.USER));
    }

    // Convertir User en AuthResponse
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "message", constant = "Opération réussie")
    @Mapping(target = "success", constant = "true")
    AuthResponse mapToAuthResponse(User user);
}