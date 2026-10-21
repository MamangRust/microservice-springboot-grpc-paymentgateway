package com.sanedge.payment_gateway.service.impl.merchant;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants;
import com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsByApiKey;
import com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsById;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantTransactionResponse;
import com.sanedge.payment_gateway.models.merchants.MerchantTransactions;
import com.sanedge.payment_gateway.repository.merchants.MerchantTransactionRepository;
import com.sanedge.payment_gateway.service.merchant.MerchantTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantTransactionImplService implements MerchantTransactionService {
        private final MerchantTransactionRepository merchantTransactionRepository;

        @Override
        public ApiResponsePagination<List<MerchantTransactionResponse>> findAll(FindAllMerchants req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all merchant transactions | Page: {}, Size: {}, Search: {}",
                                        page + 1, pageSize, keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantTransactions> merchantPage = merchantTransactionRepository
                                        .findAllTransactions(keyword, pageable);

                        List<MerchantTransactionResponse> merchantResponses = merchantPage.getContent()
                                        .stream()
                                        .map(MerchantTransactionResponse::from)
                                        .toList();

                        log.info("✅ Found {} transactions", merchantResponses.size());

                        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                                        .status("success")
                                        .data(merchantResponses)
                                        .message("Found " + merchantResponses.size() + " transactions")
                                        .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve all merchant transactions", e);
                        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to retrieve all merchant transactions")
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantTransactionResponse>> findById(FindAllMerchantTransactionsById req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching merchant transactions by merchantId={} | Page: {}, Size: {}",
                                        req.getMerchantId(), page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantTransactions> merchantPage = merchantTransactionRepository
                                        .findAllTransactionsByMerchant(
                                                        req.getMerchantId(), keyword, pageable);

                        List<MerchantTransactionResponse> merchantResponses = merchantPage.getContent()
                                        .stream()
                                        .map(MerchantTransactionResponse::from)
                                        .toList();

                        log.info("✅ Found {} transactions for merchantId={}", merchantResponses.size(),
                                        req.getMerchantId());

                        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                                        .status("success")
                                        .data(merchantResponses)
                                        .message("Found " + merchantResponses.size() + " transactions")
                                        .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve transactions for merchantId={}", req.getMerchantId(), e);
                        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to retrieve transactions for merchantId="
                                                        + req.getMerchantId())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<MerchantTransactionResponse>> findByApiKey(
                        FindAllMerchantTransactionsByApiKey req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔑 Searching merchant transactions by apiKey={} | Page: {}, Size: {}",
                                        req.getApiKey(), page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<MerchantTransactions> merchantPage = merchantTransactionRepository
                                        .findAllTransactionsByApiKey(
                                                        req.getApiKey(), keyword, pageable);

                        List<MerchantTransactionResponse> merchantResponses = merchantPage.getContent()
                                        .stream()
                                        .map(MerchantTransactionResponse::from)
                                        .toList();

                        log.info("✅ Found {} transactions for apiKey={}", merchantResponses.size(), req.getApiKey());

                        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                                        .status("success")
                                        .data(merchantResponses)
                                        .message("Found " + merchantResponses.size() + " transactions")
                                        .pagination(PaginationMeta.fromSpringPage(merchantPage))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to retrieve transactions for apiKey={}", req.getApiKey(), e);
                        return ApiResponsePagination.<List<MerchantTransactionResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to retrieve transactions for apiKey")
                                        .pagination(null)
                                        .build();
                }
        }
}
