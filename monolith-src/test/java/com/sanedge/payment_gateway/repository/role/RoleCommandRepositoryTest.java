package com.sanedge.payment_gateway.repository.role;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.Role;

public class RoleCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private RoleCommandRepository roleCommandRepository;

    private Long roleId;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName("ROLE_TEST_USER");
        Role savedRole = roleCommandRepository.save(role);
        this.roleId = savedRole.getRoleId();
    }

    @Test
    void shouldSaveRole() {
        Role role = new Role();
        role.setRoleName("ROLE_SUPER_ADMIN");
        Role saved = roleCommandRepository.save(role);
        assertThat(saved.getRoleId()).isNotNull();
        assertThat(saved.getRoleName()).isEqualTo("ROLE_SUPER_ADMIN");
    }

    @Test
    void shouldUpdateRole() {
        Role role = roleCommandRepository.findById(roleId).orElseThrow();
        role.setRoleName("ROLE_STAFF");
        Role updated = roleCommandRepository.save(role);
        assertThat(updated.getRoleName()).isEqualTo("ROLE_STAFF");
    }

    @Test
    void shouldTrashRole() {
        Role trashed = roleCommandRepository.trashed(roleId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldRestoreRole() {
        roleCommandRepository.trashed(roleId);
        Role restored = roleCommandRepository.restore(roleId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeletePermanent() {
        roleCommandRepository.deletePermanent(roleId);
        Optional<Role> role = roleCommandRepository.findById(roleId);
        assertThat(role).isEmpty();
    }

    @Test
    void shouldRestoreAllDeleted() {
        roleCommandRepository.trashed(roleId);
        boolean result = roleCommandRepository.restoreAllDeleted();
        entityManager.clear();
        assertThat(result).isTrue();
        Role role = roleCommandRepository.findById(roleId).orElseThrow();
        assertThat(role.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeleteAllDeleted() {
        roleCommandRepository.trashed(roleId);
        boolean result = roleCommandRepository.deleteAllDeleted();
        entityManager.clear();
        assertThat(result).isTrue();
        Optional<Role> role = roleCommandRepository.findById(roleId);
        assertThat(role).isEmpty();
    }
}