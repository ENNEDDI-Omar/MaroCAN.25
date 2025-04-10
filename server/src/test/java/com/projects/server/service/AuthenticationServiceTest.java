package com.projects.server.service;

import com.projects.server.domain.entities.User;
import com.projects.server.domain.enums.RoleType;
import com.projects.server.dto.request.LoginRequest;
import com.projects.server.dto.request.RegisterRequest;
import com.projects.server.dto.response.AuthResponse;
import com.projects.server.exceptions.AuthenticationException;
import com.projects.server.mapper.AuthMapper;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthMapper authMapper;

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

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Set.of(RoleType.USER))
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authMapper.mapToUser(any(RegisterRequest.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authMapper.mapToAuthResponse(any(User.class))).thenReturn(AuthResponse.builder()
                .message("Opération réussie")
                .success(true)
                .build());
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // Act
        AuthResponse response = authenticationService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertTrue(response.isSuccess());

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));
        verify(authMapper).mapToUser(any(RegisterRequest.class));
        verify(authMapper).mapToAuthResponse(any(User.class));
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

        Set<RoleType> roles = new HashSet<>();
        roles.add(RoleType.USER);

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(roles)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(authMapper.mapToAuthResponse(any(User.class))).thenReturn(AuthResponse.builder()
                .message("Opération réussie")
                .success(true)
                .build());
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
        verify(authMapper).mapToAuthResponse(any(User.class));
    }
}