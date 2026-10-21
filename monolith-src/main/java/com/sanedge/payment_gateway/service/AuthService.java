package com.sanedge.payment_gateway.service;

import com.sanedge.payment_gateway.domain.requests.auth.AuthRequest;
import com.sanedge.payment_gateway.domain.requests.auth.RegisterRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.auth.TokenResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;

public interface AuthService {
    public ApiResponse<TokenResponse> login(AuthRequest loginRequest);

    public ApiResponse<UserResponse> register(RegisterRequest registerRequest);

    public ApiResponse<TokenResponse> refreshToken(String refreshToken);

    public ApiResponse<UserResponse> getCurrentUser();
}
