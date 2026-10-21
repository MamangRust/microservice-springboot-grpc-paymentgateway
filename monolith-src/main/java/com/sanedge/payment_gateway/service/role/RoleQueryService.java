package com.sanedge.payment_gateway.service.role;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.role.FindAllRoles;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponseDeleteAt;

public interface RoleQueryService {
    ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req);

    ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req);

    ApiResponse<RoleResponse> findById(Long id);

    ApiResponse<List<RoleResponse>> findByUserId(Long userId);

    ApiResponse<RoleResponse> findByName(String name);
}
