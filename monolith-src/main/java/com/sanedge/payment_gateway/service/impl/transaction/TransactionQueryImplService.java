package com.sanedge.payment_gateway.service.impl.transaction;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactions;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionQueryRepository;
import com.sanedge.payment_gateway.service.transaction.TransactionQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionQueryImplService implements TransactionQueryService {
        private final TransactionQueryRepository transactionQueryRepository;

        @Override
        public ApiResponsePagination<List<TransactionResponse>> findAll(FindAllTransactions req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("🔍 Searching all transactions | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        search.isEmpty() ? "None" : search);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Transaction> pageResult = transactionQueryRepository.findTransactions(search, pageable);

                        List<TransactionResponse> data = pageResult.getContent().stream()
                                        .map(TransactionResponse::from)
                                        .toList();

                        log.info("✅ Found {} transactions", data.size());

                        return ApiResponsePagination.<List<TransactionResponse>>builder()
                                        .status("success")
                                        .message("Transactions retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch all transactions", e);
                        return ApiResponsePagination.<List<TransactionResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch transactions")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }

        }

        @Override
        public ApiResponsePagination<List<TransactionResponse>> findAllByCardNumber(FindAllTransactionCardNumber req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("💳 Searching transactions by cardNumber={} | Page: {}, Size: {}",
                                        req.getCardNumber(), page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Transaction> pageResult = transactionQueryRepository.findTransactionsByCardNumber(
                                        req.getCardNumber(),
                                        search,
                                        pageable);

                        List<TransactionResponse> data = pageResult.getContent().stream()
                                        .map(TransactionResponse::from)
                                        .toList();

                        log.info("✅ Found {} transactions for cardNumber={}", data.size(), req.getCardNumber());

                        return ApiResponsePagination.<List<TransactionResponse>>builder()
                                        .status("success")
                                        .message("Transactions retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch transactions by cardNumber={}", req.getCardNumber(), e);
                        return ApiResponsePagination.<List<TransactionResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch transactions")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }

        }

        @Override
        public ApiResponsePagination<List<TransactionResponseDeleteAt>> findByActive(FindAllTransactions req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("📂 Searching active transactions | Page: {}, Size: {}", page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Transaction> pageResult = transactionQueryRepository.findActiveTransactions(search,
                                        pageable);

                        List<TransactionResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(TransactionResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active transactions", data.size());

                        return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active transactions retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch active transactions", e);
                        return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch transactions")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }

        }

        @Override
        public ApiResponsePagination<List<TransactionResponseDeleteAt>> findByTrashed(FindAllTransactions req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("🗑️ Searching trashed transactions | Page: {}, Size: {}", page + 1, pageSize);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Transaction> pageResult = transactionQueryRepository.findTrashedTransactions(search,
                                        pageable);

                        List<TransactionResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(TransactionResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed transactions", data.size());

                        return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed transactions retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed transactions", e);
                        return ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch transactions")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }

        }

        @Override
        public ApiResponse<TransactionResponse> findById(Long transactionId) {
                log.info("🔍 Finding transaction by id={}", transactionId);

                Transaction transaction = transactionQueryRepository.findById(transactionId)
                                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

                log.info("✅ Transaction found, id={}", transactionId);

                return ApiResponse.<TransactionResponse>builder()
                                .status("success")
                                .message("Transaction retrieved successfully")
                                .data(TransactionResponse.from(transaction))
                                .build();
        }

        @Override
        public ApiResponse<List<TransactionResponse>> findByMerchantId(Long merchantId) {
                log.info("🏪 Finding transactions by merchantId={}", merchantId);

                List<Transaction> transactions = transactionQueryRepository
                                .findTransactionsByMerchantId(merchantId);
                List<TransactionResponse> data = transactions.stream()
                                .map(TransactionResponse::from)
                                .toList();

                log.info("✅ Found {} transactions for merchantId={}", data.size(), merchantId);

                return ApiResponse.<List<TransactionResponse>>builder()
                                .status("success")
                                .message("Transactions retrieved successfully by merchant id")
                                .data(data)
                                .build();
        }
}
