package com.projects.server.controllers;

import com.projects.server.dto.request.LoginRequest;
import com.projects.server.dto.response.AuthResponse;
import com.projects.server.security.AuthenticationService;
import com.projects.server.dto.request.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    )
    {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (Exception e) {
            log.error("Erreur d'Inscription: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @Valid @RequestBody LoginRequest request
    )
    {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (Exception e) {
            log.error("Erreur de Connexion: {}", e.getMessage());
            throw e;
        }
    }
}