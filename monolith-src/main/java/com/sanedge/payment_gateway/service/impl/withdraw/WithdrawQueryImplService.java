package com.sanedge.payment_gateway.service.impl.withdraw;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdraws;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawQueryRepository;
import com.sanedge.payment_gateway.service.withdraw.WithdrawQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawQueryImplService implements WithdrawQueryService {
        private final WithdrawQueryRepository withdrawQueryRepository;

        @Override
        public ApiResponsePagination<List<WithdrawResponse>> findAll(FindAllWithdraws req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("🔍 Searching all withdraws | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        search.isEmpty() ? "None" : search);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Withdraw> pageResult = withdrawQueryRepository.findAllWithdraws(search, pageable);

                        List<WithdrawResponse> data = pageResult.getContent().stream()
                                        .map(WithdrawResponse::from)
                                        .toList();

                        return ApiResponsePagination.<List<WithdrawResponse>>builder()
                                        .status("success")
                                        .message("Withdraws retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch all withdraws", e);
                        return ApiResponsePagination.<List<WithdrawResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch withdraws")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<WithdrawResponse>> findAllByCardNumber(FindAllWithdrawCardNumber req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("💳 Searching withdraws by card number={} | Page: {}, Size: {} | Search {}",
                                        req.getCardNumber(), page + 1, pageSize, search);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Withdraw> pageResult = withdrawQueryRepository.findAllByCardNumber(req.getCardNumber(),
                                        search, pageable);

                        List<WithdrawResponse> data = pageResult.getContent().stream()
                                        .map(WithdrawResponse::from)
                                        .toList();

                        return ApiResponsePagination.<List<WithdrawResponse>>builder()
                                        .status("success")
                                        .message("Withdraws by card number retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch withdraws by card number={}", req.getCardNumber(), e);
                        return ApiResponsePagination.<List<WithdrawResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch withdraws by card number")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<WithdrawResponse> findById(Long withdrawId) {
                try {
                        Optional<Withdraw> withdrawOpt = withdrawQueryRepository.findById(withdrawId);
                        if (withdrawOpt.isEmpty()) {
                                return ApiResponse.<WithdrawResponse>builder()
                                                .status("error")
                                                .message("Withdraw not found")
                                                .data(null)
                                                .build();
                        }

                        return ApiResponse.<WithdrawResponse>builder()
                                        .status("success")
                                        .message("Withdraw retrieved successfully")
                                        .data(WithdrawResponse.from(withdrawOpt.get()))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch withdraw id={}", withdrawId, e);
                        return ApiResponse.<WithdrawResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch withdraw")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<WithdrawResponse>> findByCard(String cardNumber) {
                try {
                        List<Withdraw> withdraws = withdrawQueryRepository.findByCardNumber(cardNumber);
                        List<WithdrawResponse> data = withdraws.stream().map(WithdrawResponse::from).toList();

                        return ApiResponse.<List<WithdrawResponse>>builder()
                                        .status("success")
                                        .message("Withdraws by card number retrieved successfully")
                                        .data(data)
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch withdraws by card number={}", cardNumber, e);
                        return ApiResponse.<List<WithdrawResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch withdraws by card number")
                                        .data(Collections.emptyList())
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<WithdrawResponseDeleteAt>> findByActive(FindAllWithdraws req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Withdraw> pageResult = withdrawQueryRepository.findActiveWithdraws(search, pageable);

                        List<WithdrawResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(WithdrawResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<WithdrawResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active withdraws retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch active withdraws", e);
                        return ApiResponsePagination.<List<WithdrawResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active withdraws")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<WithdrawResponseDeleteAt>> findByTrashed(FindAllWithdraws req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("deletedAt").descending());
                        Page<Withdraw> pageResult = withdrawQueryRepository.findTrashedWithdraws(search, pageable);

                        List<WithdrawResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(WithdrawResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<WithdrawResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed withdraws retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed withdraws", e);
                        return ApiResponsePagination.<List<WithdrawResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed withdraws")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }
}
