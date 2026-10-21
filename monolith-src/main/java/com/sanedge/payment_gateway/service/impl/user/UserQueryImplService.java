package com.sanedge.payment_gateway.service.impl.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.user.FindAllUserRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.user.UserResponse;
import com.sanedge.payment_gateway.domain.responses.user.UserResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.repository.user.UserQueryRepository;
import com.sanedge.payment_gateway.service.user.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserQueryImplService implements UserQueryService {

        private final UserQueryRepository userQueryRepository;

        @Override
        public ApiResponsePagination<List<UserResponse>> findAll(FindAllUserRequest req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all users | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<User> userPage = userQueryRepository.findAllUsers(keyword, pageable);

                        List<UserResponse> userResponses = userPage.getContent().stream()
                                        .map(UserResponse::from)
                                        .toList();

                        log.info("✅ Found {} users", userResponses.size());

                        return ApiResponsePagination.<List<UserResponse>>builder()
                                        .status("success")
                                        .message("Users retrieved successfully")
                                        .data(userResponses)
                                        .pagination(PaginationMeta.fromSpringPage(userPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to retrieve all users", e);
                        return ApiResponsePagination.<List<UserResponse>>builder()
                                        .status("error")
                                        .message("Failed to retrieve users")
                                        .data(null)
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<UserResponse> findById(Long userId) {
                try {
                        log.info("🔍 Fetching user by id={}", userId);

                        User user = userQueryRepository.findByUserId(userId)
                                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                        return ApiResponse.<UserResponse>builder()
                                        .status("success")
                                        .message("User retrieved successfully")
                                        .data(UserResponse.from(user))
                                        .build();

                } catch (ResourceNotFoundException e) {
                        log.warn("⚠️ User not found | id={}", userId);
                        return ApiResponse.<UserResponse>builder()
                                        .status("error")
                                        .message("User not found")
                                        .data(null)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch user id={}", userId, e);
                        return ApiResponse.<UserResponse>builder()
                                        .status("error")
                                        .message("Failed to retrieve user")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<UserResponseDeleteAt>> findByActive(FindAllUserRequest req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Fetching active users | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<User> userPage = userQueryRepository.findActiveUsers(keyword, pageable);

                        List<UserResponseDeleteAt> userResponses = userPage.getContent().stream()
                                        .map(UserResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active users", userResponses.size());

                        return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active users retrieved successfully")
                                        .data(userResponses)
                                        .pagination(PaginationMeta.fromSpringPage(userPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to retrieve active users", e);
                        return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to retrieve active users")
                                        .data(null)
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<UserResponseDeleteAt>> findByTrashed(FindAllUserRequest req) {
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🗑️ Fetching trashed users | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<User> userPage = userQueryRepository.findTrashedUsers(keyword, pageable);

                        List<UserResponseDeleteAt> userResponses = userPage.getContent().stream()
                                        .map(UserResponseDeleteAt::from)
                                        .toList();

                        log.info("🗑️ Found {} trashed users", userResponses.size());

                        return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed users retrieved successfully")
                                        .data(userResponses)
                                        .pagination(PaginationMeta.fromSpringPage(userPage))
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to retrieve trashed users", e);
                        return ApiResponsePagination.<List<UserResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to retrieve trashed users")
                                        .data(null)
                                        .pagination(null)
                                        .build();
                }
        }
}
