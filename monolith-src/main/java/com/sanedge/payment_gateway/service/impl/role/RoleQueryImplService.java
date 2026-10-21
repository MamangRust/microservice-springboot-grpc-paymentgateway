package com.sanedge.payment_gateway.service.impl.role;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.role.FindAllRoles;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponse;
import com.sanedge.payment_gateway.domain.responses.role.RoleResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.Role;
import com.sanedge.payment_gateway.repository.role.RoleQueryRepository;
import com.sanedge.payment_gateway.service.role.RoleQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleQueryImplService implements RoleQueryService {
        private final RoleQueryRepository roleQueryRepository;

        @Override
        public ApiResponsePagination<List<RoleResponse>> findAll(FindAllRoles req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Role> rolePage = roleQueryRepository.findRoles(keyword, pageable);

                        List<RoleResponse> responses = rolePage.getContent()
                                        .stream()
                                        .map(RoleResponse::from)
                                        .toList();

                        log.info("✅ Found {} roles", responses.size());

                        return ApiResponsePagination.<List<RoleResponse>>builder()
                                        .status("success")
                                        .message("Get all roles success")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(rolePage))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve roles", e);
                        return ApiResponsePagination.<List<RoleResponse>>builder()
                                        .status("error")
                                        .message("Failed to get all roles")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<RoleResponseDeleteAt>> findByActive(FindAllRoles req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching active roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Role> rolePage = roleQueryRepository.findActiveRoles(keyword, pageable);

                        List<RoleResponseDeleteAt> responses = rolePage.getContent()
                                        .stream()
                                        .map(RoleResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active roles", responses.size());

                        return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Get active roles success")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(rolePage))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve active roles", e);
                        return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to get active roles")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<RoleResponseDeleteAt>> findByTrashed(FindAllRoles req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching trashed roles | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Role> rolePage = roleQueryRepository.findTrashedRoles(keyword, pageable);

                        List<RoleResponseDeleteAt> responses = rolePage.getContent()
                                        .stream()
                                        .map(RoleResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed roles", responses.size());

                        return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Get trashed roles success")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(rolePage))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve trashed roles", e);
                        return ApiResponsePagination.<List<RoleResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to get trashed roles")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<RoleResponse> findById(Long id) {
                try {
                        log.info("🔍 Finding role by id={}", id);
                        Role role = roleQueryRepository.findById((long) id)
                                        .orElseThrow(() -> {
                                                log.error("❌ Role not found id={}", id);
                                                return new ResourceNotFoundException("Role not found");
                                        });

                        return ApiResponse.<RoleResponse>builder()
                                        .status("success")
                                        .message("Get role success")
                                        .data(RoleResponse.from(role))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve role id={}", id, e);
                        return ApiResponse.<RoleResponse>builder()
                                        .status("error")
                                        .message("Failed to get role")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<RoleResponse>> findByUserId(Long userId) {
                try {
                        log.info("🔍 Finding roles by user_id={}", userId);
                        List<Role> roles = roleQueryRepository.findUserRoles(userId);

                        List<RoleResponse> responses = roles.stream()
                                        .map(RoleResponse::from)
                                        .toList();

                        return ApiResponse.<List<RoleResponse>>builder()
                                        .status("success")
                                        .message("Get roles by user success")
                                        .data(responses)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve roles by user_id={}", userId, e);
                        return ApiResponse.<List<RoleResponse>>builder()
                                        .status("error")
                                        .message("Failed to get roles by user")
                                        .data(Collections.emptyList())
                                        .build();
                }
        }

        @Override
        public ApiResponse<RoleResponse> findByName(String name) {
                try {
                        log.info("🔍 Finding role by name={}", name);
                        Role role = roleQueryRepository.findByRoleName(name)
                                        .orElseThrow(() -> {
                                                log.error("❌ Role not found name={}", name);
                                                return new ResourceNotFoundException("Role not found");
                                        });

                        return ApiResponse.<RoleResponse>builder()
                                        .status("success")
                                        .message("Get role success")
                                        .data(RoleResponse.from(role))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve role name={}", name, e);
                        return ApiResponse.<RoleResponse>builder()
                                        .status("error")
                                        .message("Failed to get role")
                                        .data(null)
                                        .build();
                }
        }
}