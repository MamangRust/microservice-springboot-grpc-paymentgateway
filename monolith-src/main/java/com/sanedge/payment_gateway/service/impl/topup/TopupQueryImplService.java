package com.sanedge.payment_gateway.service.impl.topup;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopups;
import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopupsByCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.topup.TopupQueryRepository;
import com.sanedge.payment_gateway.service.topup.TopupQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupQueryImplService implements TopupQueryService {
        private final TopupQueryRepository topupQueryRepository;

        @Override
        public ApiResponsePagination<List<TopupResponse>> findAll(FindAllTopups req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("🔍 Searching all topups | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        search.isEmpty() ? "None" : search);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Topup> pageResult = topupQueryRepository.findTopups(search, pageable);

                        List<TopupResponse> data = pageResult.getContent().stream()
                                        .map(TopupResponse::from)
                                        .toList();

                        log.info("✅ Found {} topups", data.size());

                        return ApiResponsePagination.<List<TopupResponse>>builder()
                                        .status("success")
                                        .data(data)
                                        .message("Found " + data.size() + " topups")
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch all topups", e);
                        return ApiResponsePagination.<List<TopupResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch all topups")
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<TopupResponse>> findAllByCardNumber(FindAllTopupsByCardNumber req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String cardNumber = req.getCardNumber();
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching topups by card number: {} | Page: {}, Size: {}, Search: {}", cardNumber,
                                        page + 1, pageSize, keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Topup> pageResult = topupQueryRepository.findTopupByCard(cardNumber, keyword, pageable);

                        List<TopupResponse> data = pageResult.getContent().stream()
                                        .map(TopupResponse::from)
                                        .toList();

                        log.info("✅ Found {} topups for card {}", data.size(), cardNumber);

                        return ApiResponsePagination.<List<TopupResponse>>builder()
                                        .status("success")
                                        .data(data)
                                        .message("Found " + data.size() + " topups for card=" + cardNumber)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch topups for card={}", req.getCardNumber(), e);
                        return ApiResponsePagination.<List<TopupResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .message("Failed to fetch topups for card=" + req.getCardNumber())
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<TopupResponseDeleteAt>> findActive(FindAllTopups req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Topup> pageResult = topupQueryRepository.findActiveTopups(search, pageable);

                        List<TopupResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(TopupResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                                        .status("success")
                                        .data(data)
                                        .message("Found " + data.size() + " active topups")
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active topups", e);
                        return ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .message("Failed to fetch active topups")
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<TopupResponseDeleteAt>> findTrashed(FindAllTopups req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Topup> pageResult = topupQueryRepository.findTrashedTopups(search, pageable);

                        List<TopupResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(TopupResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                                        .status("success")
                                        .data(data)
                                        .message("Found " + data.size() + " trashed topups")
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed topups", e);
                        return ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch trashed topups")
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TopupResponse>> findByCard(String cardNumber) {
                try {
                        List<Topup> topups = topupQueryRepository.findByCardNumber(cardNumber);
                        List<TopupResponse> data = topups.stream()
                                        .map(TopupResponse::from)
                                        .toList();

                        log.info("✅ Retrieved {} topups for card {}", data.size(), cardNumber);

                        return ApiResponse.<List<TopupResponse>>builder()
                                        .status("success")
                                        .message("Found " + data.size() + " topups for card=" + cardNumber)
                                        .data(data)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch topups for card={}", cardNumber, e);
                        return ApiResponse.<List<TopupResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch topups for card=" + cardNumber)
                                        .build();
                }
        }

        @Override
        public ApiResponse<TopupResponse> findById(Long topupId) {
                try {
                        Topup topup = topupQueryRepository.findById(topupId)
                                        .orElseThrow(() -> {
                                                log.error("❌ Topup not found id={}", topupId);
                                                return new ResourceNotFoundException("Topup not found");
                                        });

                        log.info("✅ Retrieved topup id={}", topupId);

                        return ApiResponse.<TopupResponse>builder()
                                        .status("success")
                                        .data(TopupResponse.from(topup))
                                        .message("Found topup id=" + topupId)
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch topup id={}", topupId, e);
                        return ApiResponse.<TopupResponse>builder()
                                        .status("error")
                                        .data(null)
                                        .message("Failed to fetch topup id=" + topupId)
                                        .build();
                }
        }

}
