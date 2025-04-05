package com.projects.server.service;

import com.projects.server.domain.entities.Role;
import com.projects.server.domain.entities.User;
import com.projects.server.domain.enums.RoleType;
import com.projects.server.dto.request.LoginRequest;
import com.projects.server.dto.request.RegisterRequest;
import com.projects.server.dto.response.AuthResponse;
import com.projects.server.exceptions.AuthenticationException;
import com.projects.server.repositories.RoleRepository;
import com.projects.server.repositories.UserRepository;
import com.projects.server.security.AuthenticationService;
import com.projects.server.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerShouldCreateNewUser() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Test@1234")
                .build();

        Role role = Role.builder()
                .id(1L)
                .name(RoleType.USER)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(roleRepository.findByName(any(RoleType.class))).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // Act
        AuthResponse response = authenticationService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("Utilisateur enregistré avec succès", response.getMessage());
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertTrue(response.isSuccess());

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
    }

    @Test
    void registerShouldThrowExceptionWhenUsernameExists() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .email("test@example.com")
                .password("Test@1234")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        // Act & Assert
        Exception exception = assertThrows(AuthenticationException.class, () -> {
            authenticationService.register(request);
        });

        assertEquals("Le nom d'utilisateur est déjà pris, choisissez-en un autre", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateShouldReturnTokensWhenCredentialsAreValid() {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Test@1234")
                .build();

        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().name(RoleType.USER).build());

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(roles)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // Act
        AuthResponse response = authenticationService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertTrue(response.isSuccess());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
    }
}