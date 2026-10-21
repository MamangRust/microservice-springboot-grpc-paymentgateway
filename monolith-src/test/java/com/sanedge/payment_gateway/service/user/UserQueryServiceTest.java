package com.sanedge.payment_gateway.service.user;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.user.FindAllUserRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;

public class UserQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @BeforeEach
    void setUp() {
        userCommandRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        userCommandRepository.save(user);
    }

    @Test
    void shouldFindAll() {
        FindAllUserRequest req = new FindAllUserRequest();
        req.setPage(1);
        req.setPageSize(10);
        ApiResponsePagination<List<UserResponse>> response = userQueryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void shouldFindById() {
        User user = userCommandRepository.findAll().get(0);
        ApiResponse<UserResponse> response = userQueryService.findById(user.getUserId());
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldFindByActive() {
        FindAllUserRequest req = new FindAllUserRequest();
        ApiResponsePagination<List<UserResponseDeleteAt>> response = userQueryService.findByActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByTrashed() {
        FindAllUserRequest req = new FindAllUserRequest();
        ApiResponsePagination<List<UserResponseDeleteAt>> response = userQueryService.findByTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}