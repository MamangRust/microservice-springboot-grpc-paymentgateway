package com.sanedge.payment_gateway.service.user;

import java.util.UUID;

        import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.user.CreateUserRequest;
import com.sanedge.payment_gateway.domain.requests.user.UpdateUserRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;

public class UserCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup needed, relying on @Transactional rollbacks
    }

    @Test
    void shouldCreateUser() {
        CreateUserRequest req = new CreateUserRequest();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        req.setUsername("u_" + uniqueId);
        req.setFirstname("Test");
        req.setLastname("User");
        req.setEmail("t_" + uniqueId + "@example.com");
        req.setPassword("password123");
        req.setConfirmPassword("password123");

        ApiResponse<UserResponse> response = userCommandService.create(req);
        entityManager.flush();
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldUpdateUser() {
        CreateUserRequest createReq = new CreateUserRequest();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        createReq.setUsername("u_" + uniqueId);
        createReq.setFirstname("Test");
        createReq.setLastname("User");
        createReq.setEmail("t_" + uniqueId + "@example.com");
        createReq.setPassword("password123");
        createReq.setConfirmPassword("password123");
        ApiResponse<UserResponse> saved = userCommandService.create(createReq);

        UpdateUserRequest updateReq = new UpdateUserRequest();
        updateReq.setId(saved.getData().getId());
        updateReq.setFirstname("Updated");

        ApiResponse<UserResponse> response = userCommandService.update(updateReq);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getFirstname()).isEqualTo("Updated");
    }

    @Test
    void shouldTrashUser() {
        CreateUserRequest createReq = new CreateUserRequest();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        createReq.setUsername("u_" + uniqueId);
        createReq.setFirstname("Test");
        createReq.setLastname("User");
        createReq.setEmail("t_" + uniqueId + "@example.com");
        createReq.setPassword("password123");
        createReq.setConfirmPassword("password123");
        ApiResponse<UserResponse> saved = userCommandService.create(createReq);

        ApiResponse<UserResponseDeleteAt> response = userCommandService.trashed(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldRestoreUser() {
        CreateUserRequest createReq = new CreateUserRequest();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        createReq.setUsername("u_" + uniqueId);
        createReq.setFirstname("Test");
        createReq.setLastname("User");
        createReq.setEmail("t_" + uniqueId + "@example.com");
        createReq.setPassword("password123");
        createReq.setConfirmPassword("password123");
        ApiResponse<UserResponse> saved = userCommandService.create(createReq);
        entityManager.flush();
        userCommandService.trashed(saved.getData().getId());
        entityManager.flush();

        ApiResponse<UserResponseDeleteAt> response = userCommandService.restore(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeletePermanent() {
        CreateUserRequest createReq = new CreateUserRequest();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        createReq.setUsername("u_" + uniqueId);
        createReq.setFirstname("Test");
        createReq.setLastname("User");
        createReq.setEmail("t_" + uniqueId + "@example.com");
        createReq.setPassword("password123");
        createReq.setConfirmPassword("password123");
        ApiResponse<UserResponse> saved = userCommandService.create(createReq);

        ApiResponse<Boolean> response = userCommandService.deletePermanent(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void shouldRestoreAll() {
        ApiResponse<Boolean> response = userCommandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteAll() {
        ApiResponse<Boolean> response = userCommandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }
}