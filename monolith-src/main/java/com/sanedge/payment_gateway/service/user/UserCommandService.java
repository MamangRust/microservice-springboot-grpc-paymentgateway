package com.sanedge.payment_gateway.service.user;

import com.sanedge.payment_gateway.domain.requests.user.CreateUserRequest;
import com.sanedge.payment_gateway.domain.requests.user.UpdateUserRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponseDeleteAt;

public interface UserCommandService {

    ApiResponse<UserResponse> create(CreateUserRequest req);

    ApiResponse<UserResponse> update(UpdateUserRequest req);

    ApiResponse<UserResponseDeleteAt> trashed(Long userId);

    ApiResponse<UserResponseDeleteAt> restore(Long userId);

    ApiResponse<Boolean> deletePermanent(Long userId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}