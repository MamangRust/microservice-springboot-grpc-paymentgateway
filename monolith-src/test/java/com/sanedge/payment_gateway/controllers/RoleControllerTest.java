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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.role.CreateRoleRequest;
import com.sanedge.payment_gateway.domain.requests.role.UpdateRoleRequest;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.repository.role.RoleCommandRepository;

@AutoConfigureMockMvc
public class RoleControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleCommandRepository roleCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long roleId;

    @Autowired
    private com.sanedge.payment_gateway.repository.user_role.UserRoleRepository userRoleRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // base admin role might be seeded, but we create a new one for testing
        Role role = new Role();
        role.setRoleName("ROLE_TEST_" + java.util.UUID.randomUUID());
        Role saved = roleCommandRepository.save(role);
        roleId = saved.getRoleId();
        entityManager.flush();
    }

    @Test
    void shouldFindAllRoles() throws Exception {
        mockMvc.perform(get("/api/roles").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindActiveRoles() throws Exception {
        mockMvc.perform(get("/api/roles/active").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindTrashedRoles() throws Exception {
        roleCommandRepository.trashed(roleId);
        mockMvc.perform(get("/api/roles/trashed").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetRoleById() throws Exception {
        mockMvc.perform(get("/api/roles/" + roleId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateRole() throws Exception {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("ROLE_NEW");

        mockMvc.perform(post("/api/roles/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateRole() throws Exception {
        UpdateRoleRequest request = new UpdateRoleRequest();
        request.setId(roleId);
        request.setName("ROLE_UPDATED");

        mockMvc.perform(post("/api/roles/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashRole() throws Exception {
        mockMvc.perform(post("/api/roles/trash/" + roleId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldRestoreRole() throws Exception {
        roleCommandRepository.trashed(roleId);
        mockMvc.perform(post("/api/roles/restore/" + roleId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/roles/" + roleId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}