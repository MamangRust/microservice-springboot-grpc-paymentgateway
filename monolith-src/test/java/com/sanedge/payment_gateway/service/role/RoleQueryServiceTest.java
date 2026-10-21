package com.sanedge.payment_gateway.service.role;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.role.FindAllRoles;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponseDeleteAt;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.repository.role.RoleCommandRepository;

public class RoleQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private RoleQueryService roleQueryService;

    @Autowired
    private RoleCommandRepository roleCommandRepository;

    @BeforeEach
    void setUp() {
        // Roles are ensured by BaseIntegrationTest.
        // We can just clear other things if needed, but for RoleQueryServiceTest
        // we probably want to keep the roles.
    }

    @Test
    void shouldFindAll() {
        FindAllRoles req = new FindAllRoles();
        req.setPage(0);
        req.setPageSize(10);
        ApiResponsePagination<List<RoleResponse>> response = roleQueryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(2);
    }

    @Test
    void shouldFindByActive() {
        FindAllRoles req = new FindAllRoles();
        ApiResponsePagination<List<RoleResponseDeleteAt>> response = roleQueryService.findByActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByTrashed() {
        FindAllRoles req = new FindAllRoles();
        ApiResponsePagination<List<RoleResponseDeleteAt>> response = roleQueryService.findByTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindById() {
        Role role = roleCommandRepository.findAll().get(0);
        ApiResponse<RoleResponse> response = roleQueryService.findById(role.getRoleId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByUserId() {
        ApiResponse<List<RoleResponse>> response = roleQueryService.findByUserId(1L);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByName() {
        ApiResponse<RoleResponse> response = roleQueryService.findByName("ROLE_USER");
        assertThat(response.getStatus()).isEqualTo("success");
    }
}