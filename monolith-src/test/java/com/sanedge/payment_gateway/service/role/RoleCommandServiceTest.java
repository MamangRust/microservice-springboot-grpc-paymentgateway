package com.sanedge.payment_gateway.service.role;

        import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.role.CreateRoleRequest;
import com.sanedge.payment_gateway.domain.requests.role.UpdateRoleRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponseDeleteAt;
import com.sanedge.payment_gateway.repository.role.RoleCommandRepository;

public class RoleCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private RoleCommandService roleCommandService;

    @Autowired
    private RoleCommandRepository roleCommandRepository;

    @BeforeEach
    void setUp() {
        // Roles are ensured by BaseIntegrationTest.
    }

    @Test
    void shouldCreateRole() {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("ROLE_NEW_TEST");
        ApiResponse<RoleResponse> response = roleCommandService.create(request);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldUpdateRole() {
        CreateRoleRequest createReq = new CreateRoleRequest();
        createReq.setName("ROLE_FOR_UPDATE");
        ApiResponse<RoleResponse> saved = roleCommandService.create(createReq);

        UpdateRoleRequest updateReq = new UpdateRoleRequest();
        updateReq.setId(saved.getData().getId());
        updateReq.setName("ROLE_UPDATED_RESULT");

        ApiResponse<RoleResponse> response = roleCommandService.update(updateReq);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getName()).isEqualTo("ROLE_UPDATED_RESULT");
    }

    @Test
    void shouldTrashRole() {
        CreateRoleRequest createReq = new CreateRoleRequest();
        createReq.setName("ROLE_FOR_TRASH");
        ApiResponse<RoleResponse> saved = roleCommandService.create(createReq);

        ApiResponse<RoleResponseDeleteAt> response = roleCommandService.trash(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldRestoreRole() {
        CreateRoleRequest createReq = new CreateRoleRequest();
        createReq.setName("ROLE_FOR_RESTORE");
        ApiResponse<RoleResponse> saved = roleCommandService.create(createReq);
        roleCommandService.trash(saved.getData().getId());

        ApiResponse<RoleResponseDeleteAt> response = roleCommandService.restore(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteRole() {
        CreateRoleRequest createReq = new CreateRoleRequest();
        createReq.setName("ROLE_FOR_DELETE");
        ApiResponse<RoleResponse> saved = roleCommandService.create(createReq);

        ApiResponse<Boolean> response = roleCommandService.delete(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void shouldRestoreAll() {
        ApiResponse<Boolean> response = roleCommandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteAll() {
        ApiResponse<Boolean> response = roleCommandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }
}