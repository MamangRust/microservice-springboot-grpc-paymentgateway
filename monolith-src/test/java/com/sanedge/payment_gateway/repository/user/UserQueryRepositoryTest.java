package com.sanedge.payment_gateway.repository.user;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.User;

public class UserQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @BeforeEach
    void setUp() {
        // entityManager.clear() is already called in baseSetup.
        // admin@example.com is already inserted by baseSetup.

        if (userQueryRepository.findByEmail("test@example.com").isEmpty()) {
            User user = new User();
            user.setUsername("testuser");
            user.setFirstname("Test");
            user.setLastname("User");
            user.setEmail("test@example.com");
            user.setPassword("password");
            userCommandRepository.save(user);
        }

        // We already have admin@example.com from baseSetup with username "admin".
        // The tests expect it to have username "adminuser" or we update the tests.
        // Let's update the pre-seeded admin in baseSetup or just insert a third one here if needed.
        // Actually, let's just use the one from baseSetup and adjust expectations.
    }

    @Test
    void shouldFindAllUsers() {
        Page<User> users = userQueryRepository.findAllUsers("Test", PageRequest.of(0, 10));
        assertThat(users.getContent()).hasSize(1);
        assertThat(users.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldFindActiveUsers() {
        Page<User> users = userQueryRepository.findActiveUsers(null, PageRequest.of(0, 10));
        assertThat(users.getContent()).hasSize(3);
    }

    @Test
    void shouldFindTrashedUsers() {
        User user = userQueryRepository.findByUsername("testuser").orElseThrow();
        userCommandRepository.trashed(user.getUserId());

        Page<User> users = userQueryRepository.findTrashedUsers(null, PageRequest.of(0, 10));
        assertThat(users.getContent()).hasSize(1);
        assertThat(users.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldFindByUserId() {
        User user = userQueryRepository.findByUsername("admin").orElseThrow();
        Optional<User> found = userQueryRepository.findByUserId(user.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
    }

    @Test
    void shouldFindByEmail() {
        Optional<User> found = userQueryRepository.findByEmail("admin@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("admin");
    }

    @Test
    void shouldFindByUsername() {
        Optional<User> found = userQueryRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}