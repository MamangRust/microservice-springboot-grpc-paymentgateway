package com.sanedge.payment_gateway.service.impl.transfer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.transfers.FindAllTransfers;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.transfer.TransferQueryRepository;
import com.sanedge.payment_gateway.service.transfer.TransferQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferQueryImplService implements TransferQueryService {
        private final TransferQueryRepository transferQueryRepository;

        @Override
        public ApiResponsePagination<List<TransferResponse>> findAll(FindAllTransfers req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        log.info("🔍 Searching all transfers | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        search.isEmpty() ? "None" : search);

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Transfer> pageResult = transferQueryRepository.findTransfers(search, pageable);

                        List<TransferResponse> data = pageResult.getContent().stream()
                                        .map(TransferResponse::from)
                                        .toList();

                        log.info("✅ Found {} transfers", data.size());

                        return ApiResponsePagination.<List<TransferResponse>>builder()
                                        .status("success")
                                        .message("Transfers retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        log.error("💥 Failed to fetch transfers", e);
                        return ApiResponsePagination.<List<TransferResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch transfers")
                                        .build();
                }

        }

        @Override
        public ApiResponse<TransferResponse> findById(Long transferId) {
                Transfer transfer = transferQueryRepository.findById(transferId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Transfer not found with id " + transferId));
                return ApiResponse.<TransferResponse>builder()
                                .status("success")
                                .message("Transfer retrieved successfully")
                                .data(TransferResponse.from(transfer))
                                .build();
        }

        @Override
        public ApiResponsePagination<List<TransferResponseDeleteAt>> findByActive(FindAllTransfers req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
                        Page<Transfer> pageResult = transferQueryRepository.findActiveTransfers(search, pageable);

                        List<TransferResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(TransferResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active transfers retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        return ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active transfers")
                                        .build();
                }

        }

        @Override
        public ApiResponsePagination<List<TransferResponseDeleteAt>> findByTrashed(FindAllTransfers req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String search = req.getSearch() != null ? req.getSearch() : "";

                        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("deletedAt").descending());
                        Page<Transfer> pageResult = transferQueryRepository.findTrashedTransfers(search, pageable);

                        List<TransferResponseDeleteAt> data = pageResult.getContent().stream()
                                        .map(TransferResponseDeleteAt::from)
                                        .toList();

                        return ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed transfers retrieved successfully")
                                        .data(data)
                                        .pagination(PaginationMeta.fromSpringPage(pageResult))
                                        .build();

                } catch (Exception e) {
                        return ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed transfers")
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<TransferResponse>> findByTransferFrom(String transferFrom) {
                try {
                        List<Transfer> transfers = transferQueryRepository.findTransfersBySourceCard(transferFrom);
                        List<TransferResponse> data = transfers.stream()
                                        .map(TransferResponse::from)
                                        .toList();
                        return ApiResponse.<List<TransferResponse>>builder()
                                        .status("success")
                                        .message("Transfers by sender retrieved successfully")
                                        .data(data)
                                        .build();

                } catch (Exception e) {
                        return ApiResponse.<List<TransferResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch transfers")
                                        .build();

                }

        }

        @Override
        public ApiResponse<List<TransferResponse>> findByTransferTo(String transferTo) {
                try {
                        List<Transfer> transfers = transferQueryRepository.findTransfersByDestinationCard(transferTo);
                        List<TransferResponse> data = transfers.stream()
                                        .map(TransferResponse::from)
                                        .toList();
                        return ApiResponse.<List<TransferResponse>>builder()
                                        .status("success")
                                        .message("Transfers by receiver retrieved successfully")
                                        .data(data)
                                        .build();

                } catch (Exception e) {
                        return ApiResponse.<List<TransferResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch transfers")
                                        .build();
                }

        }
}
