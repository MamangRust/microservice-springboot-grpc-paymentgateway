package com.sanedge.payment_gateway.repository.role;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.Role;

public class RoleRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private RoleQueryRepository roleQueryRepository;

    @Autowired
    private RoleCommandRepository roleCommandRepository;

    @Autowired
    private com.sanedge.payment_gateway.repository.user_role.UserRoleRepository userRoleRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
        roleCommandRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldSaveAndFindRole() {
        Role role = new Role();
        role.setRoleName("ROLE_TEST");
        roleCommandRepository.save(role);

        Optional<Role> found = roleQueryRepository.findByRoleName("ROLE_TEST");
        assertThat(found).isPresent();
        assertThat(found.get().getRoleName()).isEqualTo("ROLE_TEST");
    }

    @Test
    void shouldTrashRole() {
        Role role = new Role();
        role.setRoleName("ROLE_TRASH");
        Role saved = roleCommandRepository.save(role);

        Role trashed = roleCommandRepository.trashed(saved.getRoleId());
        assertThat(trashed.getDeletedAt()).isNotNull();
    }
}