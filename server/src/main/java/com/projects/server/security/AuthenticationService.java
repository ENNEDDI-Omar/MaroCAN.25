package com.projects.server.security;

import com.projects.server.domain.entities.Role;
import com.projects.server.domain.enums.RoleType;
import com.projects.server.domain.entities.User;
import com.projects.server.exceptions.AuthenticationException;
import com.projects.server.mapper.AuthMapper;
import com.projects.server.repositories.RoleRepository;
import com.projects.server.repositories.UserRepository;
import com.projects.server.dto.request.LoginRequest;
import com.projects.server.dto.response.AuthResponse;
import com.projects.server.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AuthenticationException("Le nom d'utilisateur est déjà pris, choisissez-en un autre");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("L'email est déjà utilisé");
        }
        // Mapper RegisterRequest vers User
        User user = authMapper.mapToUser(request);

        // Définir le mot de passe encodé (non géré par le mapper)
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Par défaut, attribuer le rôle USER (peut être remplacé dans certains cas)
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(RoleType.USER));
        }

        User savedUser = userRepository.save(user);

        // Créer la réponse
        AuthResponse response = authMapper.mapToAuthResponse(savedUser);

        // Générer les tokens JWT
        response.setAccessToken(jwtService.generateToken(savedUser));
        response.setRefreshToken(jwtService.generateRefreshToken(savedUser));

        return response;
    }

    public AuthResponse authenticate(LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Générer les tokens JWT
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .message("Utilisateur authentifié avec succès")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .success(true)
                .build();
    }
}