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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Empty;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponsePagination;
import com.sanedge.paymentgateway.common.grpc.ProtoMappers;
import com.sanedge.paymentgateway.gateway.dto.requests.transfers.CreateTransferRequest;
import com.sanedge.paymentgateway.gateway.dto.requests.transfers.FindAllTransfers;
import com.sanedge.paymentgateway.gateway.dto.requests.transfers.UpdateTransferRequest;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.TransferResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.transfer.TransferCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.transfer.TransferQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferQueryServiceGrpc.TransferQueryServiceBlockingStub transferQueryStub;
    private final TransferCommandServiceGrpc.TransferCommandServiceBlockingStub transferCommandStub;

    // ---- CRUD ----

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransferResponse>>> findAll(
            @ModelAttribute FindAllTransfers req) {
        var resp = transferQueryStub.findAllTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindAllTransferRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransferResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfers(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponse>> findById(@PathVariable Long id) {
        var resp = transferQueryStub.findByIdTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransferResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfer(resp.getData()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransferResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllTransfers req) {
        var resp = transferQueryStub.findByActiveTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindAllTransferRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransferResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllTransfers req) {
        var resp = transferQueryStub.findByTrashedTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindAllTransferRequest.newBuilder()
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TransferResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfersDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/from/{transferFrom}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> findByTransferFrom(
            @PathVariable String transferFrom) {
        var resp = transferQueryStub.findTransferByTransferFrom(
                com.sanedge.paymentgateway.pb.transfer.FindTransferByTransferFromRequest.newBuilder()
                        .setTransferFrom(nvl(transferFrom))
                        .build());
        return ResponseEntity.ok(ApiResponse.<List<TransferResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfers(resp.getDataList()))
                .build());
    }

    @GetMapping("/to/{transferTo}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> findByTransferTo(@PathVariable String transferTo) {
        var resp = transferQueryStub.findTransferByTransferTo(
                com.sanedge.paymentgateway.pb.transfer.FindTransferByTransferToRequest.newBuilder()
                        .setTransferTo(nvl(transferTo))
                        .build());
        return ResponseEntity.ok(ApiResponse.<List<TransferResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfers(resp.getDataList()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<TransferResponse>> create(@RequestBody CreateTransferRequest req) {
        var resp = transferCommandStub.createTransfer(
                com.sanedge.paymentgateway.pb.transfer.CreateTransferRequest.newBuilder()
                        .setTransferFrom(nvl(req.getTransferFrom()))
                        .setTransferTo(nvl(req.getTransferTo()))
                        .setTransferAmount(nvl(req.getTransferAmount()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransferResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfer(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponse>> update(@RequestBody UpdateTransferRequest req) {
        var resp = transferCommandStub.updateTransfer(
                com.sanedge.paymentgateway.pb.transfer.UpdateTransferRequest.newBuilder()
                        .setTransferId(nvl(req.getTransferId()))
                        .setTransferFrom(nvl(req.getTransferFrom()))
                        .setTransferTo(nvl(req.getTransferTo()))
                        .setTransferAmount(nvl(req.getTransferAmount()))
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransferResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transfer(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponseDeleteAt>> trash(@PathVariable Long id) {
        var resp = transferCommandStub.trashedTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransferResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponseDeleteAt>> restore(@PathVariable Long id) {
        var resp = transferCommandStub.restoreTransfer(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(id.intValue())
                        .build());
        return ResponseEntity.ok(ApiResponse.<TransferResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.transferDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        var resp = transferCommandStub.deleteTransferPermanent(
                com.sanedge.paymentgateway.pb.transfer.FindByIdTransferRequest.newBuilder()
                        .setTransferId(id.intValue())
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
        var resp = transferCommandStub.restoreAllTransfer(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        var resp = transferCommandStub.deleteAllTransferPermanent(Empty.getDefaultInstance());
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
