package com.sanedge.payment_gateway.repository.role;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.Role;

public class RoleQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private RoleQueryRepository roleQueryRepository;

    @Autowired
    private RoleCommandRepository roleCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // ROLE_USER and ROLE_ADMIN are pre-seeded by BaseIntegrationTest
        entityManager.flush();
    }

    @Test
    void shouldFindRoles() {
        Page<Role> roles = roleQueryRepository.findRoles("USER", PageRequest.of(0, 10));
        assertThat(roles.getContent()).hasSize(1);
        assertThat(roles.getContent().get(0).getRoleName()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldFindByRoleName() {
        Optional<Role> role = roleQueryRepository.findByRoleName("ROLE_ADMIN");
        assertThat(role).isPresent();
        assertThat(role.get().getRoleName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldFindActiveRoles() {
        Page<Role> roles = roleQueryRepository.findActiveRoles(null, PageRequest.of(0, 10));
        assertThat(roles.getContent()).hasSize(2);
    }

    @Test
    void shouldFindTrashedRoles() {
        Role role = roleQueryRepository.findByRoleName("ROLE_USER").orElseThrow();
        roleCommandRepository.trashed(role.getRoleId());

        Page<Role> roles = roleQueryRepository.findTrashedRoles(null, PageRequest.of(0, 10));
        assertThat(roles.getContent()).hasSize(1);
        assertThat(roles.getContent().get(0).getRoleName()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldFindUserRoles() {
        List<Role> roles = roleQueryRepository.findUserRoles(adminUser.getUserId());
        assertThat(roles).isNotEmpty();
    }
}