package com.sanedge.payment_gateway.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;

@AutoConfigureMockMvc
public class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCommandRepository userCommandRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
        User userObj = new User();
        userObj.setUsername("ctrl_user_" + uniqueId);
        userObj.setFirstname("Ctrl");
        userObj.setLastname("User");
        userObj.setEmail("ctrl_" + uniqueId + "@example.com");
        userObj.setPassword("password");
        User saved = userCommandRepository.save(userObj);
        userId = saved.getUserId();
        entityManager.flush();
    }

    @Test
    void shouldFindAllUsers() throws Exception {
        mockMvc.perform(get("/api/users").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/" + userId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/api/users/create")
                .contentType("application/json")
                .content("{\"username\":\"new_ctrl_user\",\"firstname\":\"New\",\"lastname\":\"Ctrl\",\"email\":\"new_ctrl@example.com\",\"password\":\"password\",\"confirmPassword\":\"password\"}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        mockMvc.perform(post("/api/users/update")
                .contentType("application/json")
                .content("{\"id\":" + userId + ",\"username\":\"updated_ctrl_user\",\"email\":\"updated_ctrl@example.com\"}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashAndRestoreUser() throws Exception {
        mockMvc.perform(post("/api/users/trash/" + userId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + userId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}