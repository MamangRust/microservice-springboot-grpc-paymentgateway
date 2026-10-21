package com.sanedge.payment_gateway.service.role;

import com.sanedge.payment_gateway.domain.requests.role.CreateRoleRequest;
import com.sanedge.payment_gateway.domain.requests.role.UpdateRoleRequest;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponseDeleteAt;

public interface RoleCommandService {
    ApiResponse<RoleResponse> create(CreateRoleRequest request);

    ApiResponse<RoleResponse> update(UpdateRoleRequest request);

    ApiResponse<RoleResponseDeleteAt> trash(Long id);

    ApiResponse<RoleResponseDeleteAt> restore(Long id);

    ApiResponse<Boolean> delete(Long id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
