package com.sanedge.payment_gateway.service.impl.user;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.user.CreateUserRequest;
import com.sanedge.payment_gateway.domain.requests.user.UpdateUserRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.models.UserRole;
import com.sanedge.payment_gateway.repository.role.RoleQueryRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserQueryRepository;
import com.sanedge.payment_gateway.repository.user_role.UserRoleRepository;
import com.sanedge.payment_gateway.service.user.UserCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCommandImplService implements UserCommandService {
    private final UserQueryRepository userQueryRepository;
    private final RoleQueryRepository roleQueryRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserCommandRepository userCommandRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Override
    public ApiResponse<UserResponse> create(CreateUserRequest req) {
        validateRequest(req);

        Optional<User> existingUser = userQueryRepository.findByEmail(req.getEmail());
        if (existingUser.isPresent()) {
            log.error("❌ [REGISTER] Email already taken | Email: {}", req.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        User newUser = new User();
        newUser.setUsername(req.getUsername());
        newUser.setFirstname(req.getFirstname());
        newUser.setLastname(req.getLastname());
        newUser.setEmail(req.getEmail());
        newUser.setPassword(passwordEncoder.encode(req.getPassword()));
        newUser.setRoles(Set.of(role));

        UserRole userRole = new UserRole();

        userRole.setUser(newUser);
        userRole.setRole(role);

        try {
            newUser = userCommandRepository.save(newUser);
            userRole = userRoleRepository.save(userRole);
        } catch (Exception e) {
            log.error("❌ Failed to create user", e);
            return ApiResponse.<UserResponse>builder()
                    .status("error")
                    .message("Failed to create user")
                    .build();
        }

        UserResponse userResponse = UserResponse.from(newUser);

        return ApiResponse.<UserResponse>builder()
                .status("success")
                .message("User registered successfully")
                .data(userResponse)
                .build();
    }

    @Override
    public ApiResponse<UserResponse> update(UpdateUserRequest req) {
        validateRequest(req);

        try {
            User user = userQueryRepository.findById(req.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID " + req.getId()));

            if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
                Optional<User> existingUser = userQueryRepository.findByEmail(req.getEmail());
                if (existingUser.isPresent()) {
                    log.error("❌ [UPDATE] Email already taken | Email: {}", req.getEmail());
                    return ApiResponse.<UserResponse>builder()
                            .status("error")
                            .message("Email already registered")
                            .build();
                }
                user.setEmail(req.getEmail());
            }

            if (req.getUsername() != null)
                user.setUsername(req.getUsername());
            if (req.getFirstname() != null)
                user.setFirstname(req.getFirstname());
            if (req.getLastname() != null)
                user.setLastname(req.getLastname());

            if (req.getPassword() != null) {
                if (!req.getPassword().equals(req.getConfirmPassword())) {
                    log.error("❌ [UPDATE] Password and confirm password do not match for user ID {}", req.getId());
                    return ApiResponse.<UserResponse>builder()
                            .status("error")
                            .message("Password and confirm password do not match")
                            .build();
                }
                user.setPassword(passwordEncoder.encode(req.getPassword()));
            }

            Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN")
                    .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
            user.setRoles(new HashSet<>(Collections.singleton(role)));

            user.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            User updatedUser = userCommandRepository.save(user);

            log.info("✅ User updated successfully | User ID: {}", updatedUser.getUserId());

            return ApiResponse.<UserResponse>builder()
                    .status("success")
                    .message("✅ User updated successfully!")
                    .data(UserResponse.from(updatedUser))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update user id={}", req.getId(), e);
            return ApiResponse.<UserResponse>builder()
                    .status("error")
                    .message("Failed to update user")
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponseDeleteAt> trashed(Long userId) {
        log.info("🗑️ Trashing user id={}", userId);

        try {
            User user = userCommandRepository.trashed(userId);
            return ApiResponse.<UserResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ User trashed successfully!")
                    .data(UserResponseDeleteAt.from(user))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash user id={}", userId, e);
            return ApiResponse.<UserResponseDeleteAt>builder()
                    .status("error")
                    .message("💥 Failed to trash user")
                    .build();
        }
    }

    @Override
    public ApiResponse<UserResponseDeleteAt> restore(Long userId) {
        log.info("♻️ Restoring user id={}", userId);

        try {
            User user = userCommandRepository.restore(userId);
            return ApiResponse.<UserResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ User restored successfully!")
                    .data(UserResponseDeleteAt.from(user))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore user id={}", userId, e);
            return ApiResponse.<UserResponseDeleteAt>builder()
                    .status("error")
                    .message("💥 Failed to restore user")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long userId) {
        log.info("🧨 Permanently deleting user id={}", userId);

        try {
            userCommandRepository.deletePermanent(userId);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 User permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete user id={}", userId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to permanently delete user")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed users");

        try {
            userCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All users restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all users", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to restore all users")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed users");

        try {
            userCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All users permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all users", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to delete all users")
                    .data(false)
                    .build();
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb.toString());
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }

}
