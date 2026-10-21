package com.sanedge.payment_gateway.repository.user;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.User;

public class UserCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandRepository userCommandRepository;

    private Long userId;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
        User user = new User();
        user.setUsername("testuser_" + uniqueId);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test_" + uniqueId + "@example.com");
        user.setPassword("password");
        User savedUser = userCommandRepository.save(user);
        this.userId = savedUser.getUserId();
        entityManager.flush();
    }

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setUsername("newuser");
        user.setFirstname("New");
        user.setLastname("User");
        user.setEmail("new@example.com");
        user.setPassword("password");
        User saved = userCommandRepository.save(user);
        assertThat(saved.getUserId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");
    }

    @Test
    void shouldUpdateUser() {
        User user = userCommandRepository.findById(userId).orElseThrow();
        user.setFirstname("Updated");
        User saved = userCommandRepository.save(user);
        assertThat(saved.getFirstname()).isEqualTo("Updated");
    }

    @Test
    void shouldTrashUser() {
        User trashed = userCommandRepository.trashed(userId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldRestoreUser() {
        userCommandRepository.trashed(userId);
        User restored = userCommandRepository.restore(userId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeletePermanent() {
        userCommandRepository.deletePermanent(userId);
        Optional<User> user = userCommandRepository.findById(userId);
        assertThat(user).isEmpty();
    }

    @Test
    void shouldRestoreAllDeleted() {
        userCommandRepository.trashed(userId);
        boolean result = userCommandRepository.restoreAllDeleted();
        entityManager.clear();
        assertThat(result).isTrue();
        User user = userCommandRepository.findById(userId).orElseThrow();
        assertThat(user.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeleteAllDeleted() {
        userCommandRepository.trashed(userId);
        boolean result = userCommandRepository.deleteAllDeleted();
        entityManager.clear();
        assertThat(result).isTrue();
        Optional<User> user = userCommandRepository.findById(userId);
        assertThat(user).isEmpty();
    }
}