package com.sanedge.paymentgateway.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.gateway.dto.requests.transaction.CreateTransactionRequest;
import com.sanedge.paymentgateway.gateway.dto.requests.transaction.FindAllTransactionCardNumber;
import com.sanedge.paymentgateway.gateway.dto.requests.transaction.FindAllTransactions;
import com.sanedge.paymentgateway.gateway.dto.requests.transaction.UpdateTransactionRequest;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.TransactionResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.transaction.TransactionCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.transaction.TransactionQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionQueryServiceGrpc.TransactionQueryServiceBlockingStub transactionQueryStub;
    private final TransactionCommandServiceGrpc.TransactionCommandServiceBlockingStub transactionCommandStub;

    // ---- CRUD ----

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findAll(
            @ModelAttribute FindAllTransactions req) {
        var resp = transactionQueryStub.findAllTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findAllByCardNumber(
            @ModelAttribute FindAllTransactionCardNumber req) {
        var resp = transactionQueryStub.findAllTransactionByCardNumber(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionCardNumberRequest.newBuilder()
                        .setCardNumber(nvl(req.getCardNumber()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllTransactions req) {
        var resp = transactionQueryStub.findByActiveTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllTransactions req) {
        var resp = transactionQueryStub.findByTrashedTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindAllTransactionRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransactionResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> findById(@PathVariable Long id) {
        var resp = transactionQueryStub.findByIdTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build());
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findByMerchantId(@PathVariable Long merchantId) {
        var resp = transactionQueryStub.findTransactionByMerchantId(
                com.sanedge.paymentgateway.pb.transaction.FindTransactionByMerchantIdRequest.newBuilder()
                        .setMerchantId(merchantId.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<List<TransactionResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactions(resp.getDataList()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @RequestHeader("X-api-Key") String apiKey,
            @RequestBody CreateTransactionRequest req) {
        var resp = transactionCommandStub.createTransaction(
                com.sanedge.paymentgateway.pb.transaction.CreateTransactionRequest.newBuilder()
                        .setApiKey(nvl(apiKey))
                        .setCardNumber(nvl(req.getCardNumber()))
                        .setAmount(nvl(req.getAmount()))
                        .setPaymentMethod(nvl(req.getPaymentMethod()))
                        .setMerchantId(nvl(req.getMerchantId()))
                        .setTransactionTime(ProtoMapper.toTimestamp(req.getTransactionTime()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @RequestHeader("X-api-Key") String apiKey,
            @RequestBody UpdateTransactionRequest req) {
        var resp = transactionCommandStub.updateTransaction(
                com.sanedge.paymentgateway.pb.transaction.UpdateTransactionRequest.newBuilder()
                        .setTransactionId(nvl(req.getTransactionId()))
                        .setApiKey(nvl(apiKey))
                        .setCardNumber(nvl(req.getCardNumber()))
                        .setAmount(nvl(req.getAmount()))
                        .setPaymentMethod(nvl(req.getPaymentMethod()))
                        .setMerchantId(nvl(req.getMerchantId()))
                        .setTransactionTime(ProtoMapper.toTimestamp(req.getTransactionTime()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transaction(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> trash(@PathVariable Long id) {
        var resp = transactionCommandStub.trashedTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> restore(@PathVariable Long id) {
        var resp = transactionCommandStub.restoreTransaction(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransactionResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transactionDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        var resp = transactionCommandStub.deleteTransactionPermanent(
                com.sanedge.paymentgateway.pb.transaction.FindByIdTransactionRequest.newBuilder()
                        .setTransactionId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        var resp = transactionCommandStub.restoreAllTransaction(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        var resp = transactionCommandStub.deleteAllTransactionPermanent(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }


    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static int nvl(Long value) {
        return value == null ? 0 : value.intValue();
    }

    private static String nvl(String value) {
        return value == null ? "" : value;
    }

    private static Boolean success(String status) {
        return "success".equalsIgnoreCase(status);
    }
}
