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
import com.sanedge.paymentgateway.gateway.dto.requests.saldo.CreateSaldoRequest;
import com.sanedge.paymentgateway.gateway.dto.requests.saldo.FindAllSaldos;
import com.sanedge.paymentgateway.gateway.dto.requests.saldo.UpdateSaldoRequest;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.SaldoResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.saldo.SaldoCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.saldo.SaldoQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saldos")
public class SaldoController {

    private final SaldoQueryServiceGrpc.SaldoQueryServiceBlockingStub saldoQueryStub;
    private final SaldoCommandServiceGrpc.SaldoCommandServiceBlockingStub saldoCommandStub;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SaldoResponse>>> findAll(@ModelAttribute FindAllSaldos req) {
        var resp = saldoQueryStub.findAllSaldo(com.sanedge.paymentgateway.pb.saldo.FindAllSaldoRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<SaldoResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldos(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SaldoResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllSaldos req) {
        var resp = saldoQueryStub.findByActive(com.sanedge.paymentgateway.pb.saldo.FindAllSaldoRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<SaldoResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldosDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SaldoResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllSaldos req) {
        var resp = saldoQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.saldo.FindAllSaldoRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<SaldoResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldosDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<SaldoResponse>> findById(@PathVariable Long id) {
        var resp = saldoQueryStub.findByIdSaldo(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build());
    }

    @GetMapping("/by-card/{cardNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<SaldoResponse>> findByCard(@PathVariable String cardNumber) {
        var resp = saldoQueryStub.findByCardNumber(com.sanedge.paymentgateway.pb.card.FindByCardNumberRequest.newBuilder()
                .setCardNumber(cardNumber)
                .build());
        return ResponseEntity.ok(ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponse>> create(@RequestBody CreateSaldoRequest req) {
        var resp = saldoCommandStub.createSaldo(com.sanedge.paymentgateway.pb.saldo.CreateSaldoRequest.newBuilder()
                .setCardNumber(nvl(req.getCardNumber()))
                .setTotalBalance(nvl(req.getTotalBalance()))
                .build());
        return ResponseEntity.ok(ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponse>> update(@RequestBody UpdateSaldoRequest req) {
        var resp = saldoCommandStub.updateSaldo(com.sanedge.paymentgateway.pb.saldo.UpdateSaldoRequest.newBuilder()
                .setSaldoId(nvl(req.getSaldoId()))
                .setCardNumber(nvl(req.getCardNumber()))
                .setTotalBalance(nvl(req.getTotalBalance()))
                .build());
        return ResponseEntity.ok(ApiResponse.<SaldoResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldo(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponseDeleteAt>> trash(@PathVariable Long id) {
        var resp = saldoCommandStub.trashedSaldo(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<SaldoResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponseDeleteAt>> restore(@PathVariable Long id) {
        var resp = saldoCommandStub.restoreSaldo(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<SaldoResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.saldoDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable Long id) {
        var resp = saldoCommandStub.deleteSaldoPermanent(com.sanedge.paymentgateway.pb.saldo.FindByIdSaldoRequest.newBuilder()
                .setSaldoId(id.intValue())
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
        var resp = saldoCommandStub.restoreAllSaldo(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        var resp = saldoCommandStub.deleteAllSaldoPermanent(Empty.getDefaultInstance());
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
