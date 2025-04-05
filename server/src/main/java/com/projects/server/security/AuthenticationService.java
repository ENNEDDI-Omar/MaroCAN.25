package com.projects.server.security;

import com.projects.server.domain.entities.Role;
import com.projects.server.domain.enums.RoleType;
import com.projects.server.domain.entities.User;
import com.projects.server.exceptions.AuthenticationException;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AuthenticationException("Le nom d'utilisateur est déjà pris, choisissez-en un autre");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("L'email est déjà utilisé");
        }

        // Utiliser une nouvelle collection
        Set<Role> roles = new HashSet<>();

        // Obtenir le rôle USER
        Role userRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Rôle USER non trouvé"));

        // Ajouter le rôle à la collection
        roles.add(userRole);

        // Créer l'utilisateur sans référence circulaire
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        // Générer les tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .message("Utilisateur enregistré avec succès")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .success(true)
                .build();
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