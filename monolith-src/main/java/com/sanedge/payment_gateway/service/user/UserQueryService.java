package com.sanedge.payment_gateway.service.user;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.user.FindAllUserRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponseDeleteAt;

public interface UserQueryService {

    ApiResponsePagination<List<UserResponse>> findAll(FindAllUserRequest req);

    ApiResponse<UserResponse> findById(Long userId);

    ApiResponsePagination<List<UserResponseDeleteAt>> findByActive(FindAllUserRequest req);

    ApiResponsePagination<List<UserResponseDeleteAt>> findByTrashed(FindAllUserRequest req);
}
