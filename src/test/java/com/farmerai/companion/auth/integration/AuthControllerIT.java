package com.farmerai.companion.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmerai.companion.auth.presentation.dto.request.LoginRequest;
import com.farmerai.companion.auth.presentation.dto.request.LogoutRequest;
import com.farmerai.companion.auth.presentation.dto.request.RefreshTokenRequest;
import com.farmerai.companion.auth.presentation.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the full authentication flow.
 *
 * <p>Uses Testcontainers to spin up a real PostgreSQL instance.
 * Tests the complete register → login → refresh → logout lifecycle.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Authentication Flow Integration Test")
class AuthControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("farmerai_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String accessToken;
    private static String refreshToken;

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/auth/register — should register a new user")
    void shouldRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "farmer@test.com", "StrongP@ss1", "FARMER"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.user.email").value("farmer@test.com"))
                .andExpect(jsonPath("$.user.role").value("FARMER"))
                .andExpect(jsonPath("$.user.status").value("ACTIVE"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        accessToken = objectMapper.readTree(responseBody).get("accessToken").asText();
        refreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/v1/auth/register — should reject duplicate email")
    void shouldRejectDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "farmer@test.com", "StrongP@ss1", "FARMER"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/v1/auth/register — should reject invalid email")
    void shouldRejectInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "not-an-email", "StrongP@ss1", "FARMER"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/v1/auth/register — should reject ADMIN role self-assignment")
    void shouldRejectAdminSelfAssignment() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "admin@test.com", "StrongP@ss1", "ADMIN"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("ADMIN")));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/v1/auth/login — should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("farmer@test.com", "StrongP@ss1");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("farmer@test.com"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        refreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/v1/auth/login — should reject wrong password")
    void shouldRejectWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest("farmer@test.com", "WrongP@ss1");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/v1/auth/refresh — should refresh access token")
    void shouldRefreshToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(not(refreshToken)))
                .andReturn();

        // Update refresh token for subsequent tests (rotation)
        String responseBody = result.getResponse().getContentAsString();
        refreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/v1/auth/logout — should logout successfully")
    void shouldLogoutSuccessfully() throws Exception {
        LogoutRequest request = new LogoutRequest(refreshToken);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    @Order(9)
    @DisplayName("POST /api/v1/auth/refresh — should reject revoked refresh token")
    void shouldRejectRevokedRefreshToken() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
