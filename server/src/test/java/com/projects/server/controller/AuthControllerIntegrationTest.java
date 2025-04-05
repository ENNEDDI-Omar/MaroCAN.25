package com.projects.server.controller;

import com.projects.server.domain.entities.Role;
import com.projects.server.domain.enums.RoleType;
import com.projects.server.repositories.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.server.dto.request.LoginRequest;
import com.projects.server.dto.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setup() {
        // Créer les rôles nécessaires s'ils n'existent pas déjà
        if (roleRepository.findByName(RoleType.USER).isEmpty()) {
            Role userRole = Role.builder()
                    .name(RoleType.USER)
                    .build();
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(RoleType.ADMIN).isEmpty()) {
            Role adminRole = Role.builder()
                    .name(RoleType.ADMIN)
                    .build();
            roleRepository.save(adminRole);
        }

        // Ajouter d'autres rôles si nécessaire
    }

    @Test
    public void registerShouldReturnTokensWhenValidRequest() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("Test@1234")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    public void registerShouldReturnErrorWhenInvalidRequest() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .username("new")  // Username trop court selon vos contraintes
                .email("invalid-email")  // Email invalide
                .password("weak")  // Mot de passe trop faible
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginShouldReturnTokensWhenValidCredentials() throws Exception {
        // Arrange - Enregistrer d'abord un utilisateur
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("loginuser")
                .email("loginuser@example.com")
                .password("Test@1234")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Maintenant tester la connexion
        LoginRequest loginRequest = LoginRequest.builder()
                .email("loginuser@example.com")
                .password("Test@1234")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }
}