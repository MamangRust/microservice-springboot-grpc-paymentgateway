package com.sanedge.payment_gateway.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.auth.AuthRequest;
import com.sanedge.payment_gateway.domain.requests.auth.RegisterRequest;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;

@AutoConfigureMockMvc
public class AuthControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // No manual cleanup needed, relying on @Transactional
    }

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstname("Auth");
        req.setLastname("User");
        req.setUsername("user_" + UUID.randomUUID().toString().substring(0, 8));
        req.setEmail("auth_" + UUID.randomUUID() + "@example.com");
        req.setPassword("password123");
        req.setConfirmPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        // First register
        RegisterRequest regReq = new RegisterRequest();regReq.setFirstname("Login");
        regReq.setLastname("User");
        String loginUsername = "u_" + UUID.randomUUID().toString().substring(0, 8);
        regReq.setUsername(loginUsername);
        regReq.setEmail("login_" + UUID.randomUUID() + "@example.com");
        regReq.setPassword("password123");
        regReq.setConfirmPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));

        AuthRequest loginReq = new AuthRequest();
        loginReq.setUsername(loginUsername);
        loginReq.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCheckHealth() throws Exception {
        mockMvc.perform(get("/api/auth/healthchecker"))
                .andExpect(status().isOk());
    }
}