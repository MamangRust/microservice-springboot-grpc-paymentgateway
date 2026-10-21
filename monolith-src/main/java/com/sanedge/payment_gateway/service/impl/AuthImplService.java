package com.sanedge.payment_gateway.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanedge.payment_gateway.domain.requests.auth.AuthRequest;
import com.sanedge.payment_gateway.domain.requests.auth.RegisterRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.auth.TokenResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.models.RefreshToken;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.repository.refresh_token.RefreshTokenCommandRepository;
import com.sanedge.payment_gateway.repository.refresh_token.RefreshTokenQueryRepository;
import com.sanedge.payment_gateway.repository.role.RoleQueryRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserQueryRepository;
import com.sanedge.payment_gateway.security.JwtProvider;
import com.sanedge.payment_gateway.security.UserDetailsImpl;
import com.sanedge.payment_gateway.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthImplService implements AuthService {

        private final AuthenticationManager authenticationManager;
        private final UserQueryRepository userQueryRepository;
        private final UserCommandRepository userCommandRepository;
        private final JwtProvider jwtProvider;
        private final PasswordEncoder passwordEncoder;
        private final RefreshTokenQueryRepository refreshTokenQueryRepository;
        private final RefreshTokenCommandRepository refreshTokenCommandRepository;
        private final RoleQueryRepository roleQueryRepository;

        @Override
        public ApiResponse<TokenResponse> login(AuthRequest loginRequest) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                        User user = userQueryRepository.findByUsername(userDetails.getUsername())
                                        .orElse(null);

                        if (user == null) {
                                return ApiResponse.<TokenResponse>builder()
                                                .status("error")
                                                .message("User not found")
                                                .data(null)
                                                .build();
                        }

                        String accessToken = jwtProvider.generateAccessToken(user.getUsername());
                        String refreshTokenStr = jwtProvider.generateRefreshToken(user.getUsername());

                        Optional<RefreshToken> existingTokenOpt = refreshTokenQueryRepository
                                        .findByUserId(user.getUserId());

                        RefreshToken refreshTokenEntity;
                        if (existingTokenOpt.isPresent()) {
                                refreshTokenEntity = existingTokenOpt.get();
                                refreshTokenEntity.setToken(refreshTokenStr);
                                refreshTokenEntity.setExpiration(new Timestamp(System.currentTimeMillis()
                                                + jwtProvider.getJwtRefreshExpirationMs()));
                                refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);
                        } else {
                                refreshTokenCommandRepository.deleteByUserId(user.getUserId());
                                refreshTokenEntity = new RefreshToken();
                                refreshTokenEntity.setUser(user);
                                refreshTokenEntity.setToken(refreshTokenStr);
                                refreshTokenEntity.setExpiration(new Timestamp(System.currentTimeMillis()
                                                + jwtProvider.getJwtRefreshExpirationMs()));
                                refreshTokenEntity = refreshTokenCommandRepository.save(refreshTokenEntity);
                        }

                        TokenResponse tokenResponse = TokenResponse.builder()
                                        .access_token(accessToken)
                                        .refresh_token(refreshTokenEntity.getToken())
                                        .build();

                        return ApiResponse.<TokenResponse>builder()
                                        .status("success")
                                        .message("Login successful")
                                        .data(tokenResponse)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Login failed for user {}", loginRequest.getUsername(), e);
                        return ApiResponse.<TokenResponse>builder()
                                        .status("error")
                                        .message("Login failed")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<UserResponse> register(RegisterRequest registerRequest) {
                try {
                        Optional<User> existingUser = userQueryRepository.findByEmail(registerRequest.getEmail());
                        if (existingUser.isPresent()) {
                                return ApiResponse.<UserResponse>builder()
                                                .status("error")
                                                .message("Email already registered")
                                                .data(null)
                                                .build();
                        }

                        Role role = roleQueryRepository.findByRoleName("ROLE_ADMIN").orElse(null);
                        if (role == null) {
                                return ApiResponse.<UserResponse>builder()
                                                .status("error")
                                                .message("Default role not found")
                                                .data(null)
                                                .build();
                        }

                        User newUser = new User();
                        newUser.setUsername(registerRequest.getUsername());
                        newUser.setFirstname(registerRequest.getFirstname());
                        newUser.setLastname(registerRequest.getLastname());
                        newUser.setEmail(registerRequest.getEmail());
                        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                        newUser.setRoles(Set.of(role));

                        newUser = userCommandRepository.save(newUser);

                        UserResponse userResponse = UserResponse.from(newUser);

                        return ApiResponse.<UserResponse>builder()
                                        .status("success")
                                        .message("User registered successfully")
                                        .data(userResponse)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to register user {}: {}", registerRequest.getEmail(), e.getMessage(), e);
                        return ApiResponse.<UserResponse>builder()
                                        .status("error")
                                        .message("Failed to register user")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<TokenResponse> refreshToken(String refreshToken) {
                try {
                        RefreshToken storedToken = refreshTokenQueryRepository.findByToken(refreshToken).orElse(null);

                        if (storedToken == null) {
                                return ApiResponse.<TokenResponse>builder()
                                                .status("error")
                                                .message("Refresh token not found")
                                                .data(null)
                                                .build();
                        }

                        if (storedToken.getExpiration().before(new Date())) {
                                return ApiResponse.<TokenResponse>builder()
                                                .status("error")
                                                .message("Refresh token expired")
                                                .data(null)
                                                .build();
                        }

                        User user = storedToken.getUser();
                        String newAccessToken = jwtProvider.generateAccessToken(user.getUsername());
                        storedToken.setExpiration(new Timestamp(
                                        System.currentTimeMillis() + jwtProvider.getJwtRefreshExpirationMs()));
                        refreshTokenCommandRepository.save(storedToken);

                        TokenResponse tokenResponse = TokenResponse.builder()
                                        .access_token(newAccessToken)
                                        .refresh_token(refreshToken)
                                        .build();

                        return ApiResponse.<TokenResponse>builder()
                                        .status("success")
                                        .message("Access token refreshed successfully")
                                        .data(tokenResponse)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to refresh token {}", refreshToken, e);
                        return ApiResponse.<TokenResponse>builder()
                                        .status("error")
                                        .message("Failed to refresh token")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<UserResponse> getCurrentUser() {
                try {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        User user = userQueryRepository.findByUsername(authentication.getName()).orElse(null);

                        if (user == null) {
                                return ApiResponse.<UserResponse>builder()
                                                .status("error")
                                                .message("User not found")
                                                .data(null)
                                                .build();
                        }

                        UserResponse userResponse = UserResponse.from(user);

                        return ApiResponse.<UserResponse>builder()
                                        .status("success")
                                        .message("Current user fetched successfully")
                                        .data(userResponse)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch current user", e);
                        return ApiResponse.<UserResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch current user")
                                        .data(null)
                                        .build();
                }
        }
}
