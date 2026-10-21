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
import com.sanedge.paymentgateway.gateway.dto.requests.topup.CreateTopupRequest;
import com.sanedge.paymentgateway.gateway.dto.requests.topup.FindAllTopups;
import com.sanedge.paymentgateway.gateway.dto.requests.topup.FindAllTopupsByCardNumber;
import com.sanedge.paymentgateway.gateway.dto.requests.topup.UpdateTopupRequest;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.TopupResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.topup.TopupResponseDeleteAt;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.topup.TopupCommandServiceGrpc;
import com.sanedge.paymentgateway.pb.topup.TopupQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topups")
public class TopupController {

    private final TopupQueryServiceGrpc.TopupQueryServiceBlockingStub topupQueryStub;
    private final TopupCommandServiceGrpc.TopupCommandServiceBlockingStub topupCommandStub;

    // ---- CRUD ----

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponse>>> findAll(@ModelAttribute FindAllTopups req) {
        var resp = topupQueryStub.findAllTopup(com.sanedge.paymentgateway.pb.topup.FindAllTopupRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TopupResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topups(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponse>>> findAllByCardNumber(
            @ModelAttribute FindAllTopupsByCardNumber req) {
        var resp = topupQueryStub.findAllTopupByCardNumber(
                com.sanedge.paymentgateway.pb.topup.FindAllTopupByCardNumberRequest.newBuilder()
                        .setCardNumber(nvl(req.getCardNumber()))
                        .setPage(nvl(req.getPage()))
                        .setPageSize(nvl(req.getPageSize()))
                        .setSearch(nvl(req.getSearch()))
                        .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TopupResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topups(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllTopups req) {
        var resp = topupQueryStub.findByActive(com.sanedge.paymentgateway.pb.topup.FindAllTopupRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllTopups req) {
        var resp = topupQueryStub.findByTrashed(com.sanedge.paymentgateway.pb.topup.FindAllTopupRequest.newBuilder()
                .setPage(nvl(req.getPage()))
                .setPageSize(nvl(req.getPageSize()))
                .setSearch(nvl(req.getSearch()))
                .build());
        return ResponseEntity.ok(ApiResponsePagination.<List<TopupResponseDeleteAt>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupsDeleteAt(resp.getDataList()))
                .pagination(ProtoMappers.fromProtoPagination(resp.getPaginationMeta()))
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<TopupResponse>> findById(@PathVariable Long id) {
        var resp = topupQueryStub.findByIdTopup(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<TopupResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topup(resp.getData()))
                .build());
    }

    @GetMapping("/card/{cardNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<List<TopupResponse>>> findByCard(@PathVariable String cardNumber) {
        var resp = topupQueryStub.findByCardNumberTopup(
                com.sanedge.paymentgateway.pb.topup.FindByCardNumberTopupRequest.newBuilder()
                        .setCardNumber(nvl(cardNumber))
                        .build());
        return ResponseEntity.ok(ApiResponse.<List<TopupResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(resp.hasData() ? List.of(ProtoMapper.topup(resp.getData())) : List.of())
                .build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TopupResponse>> create(@RequestBody CreateTopupRequest req) {
        var resp = topupCommandStub.createTopup(com.sanedge.paymentgateway.pb.topup.CreateTopupRequest.newBuilder()
                .setCardNumber(nvl(req.getCardNumber()))
                .setTopupNo("")
                .setTopupAmount(nvl(req.getTopupAmount()))
                .setTopupMethod(nvl(req.getTopupMethod()))
                .build());
        return ResponseEntity.ok(ApiResponse.<TopupResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topup(resp.getData()))
                .build());
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TopupResponse>> update(@RequestBody UpdateTopupRequest req) {
        var resp = topupCommandStub.updateTopup(com.sanedge.paymentgateway.pb.topup.UpdateTopupRequest.newBuilder()
                .setTopupId(nvl(req.getTopupId()))
                .setCardNumber(nvl(req.getCardNumber()))
                .setTopupAmount(nvl(req.getTopupAmount()))
                .setTopupMethod(nvl(req.getTopupMethod()))
                .build());
        return ResponseEntity.ok(ApiResponse.<TopupResponse>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topup(resp.getData()))
                .build());
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopupResponseDeleteAt>> trash(@PathVariable Long id) {
        var resp = topupCommandStub.trashedTopup(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<TopupResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupDeleteAt(resp.getData()))
                .build());
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopupResponseDeleteAt>> restore(@PathVariable Long id) {
        var resp = topupCommandStub.restoreTopup(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(id.intValue())
                .build());
        return ResponseEntity.ok(ApiResponse.<TopupResponseDeleteAt>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.topupDeleteAt(resp.getData()))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        var resp = topupCommandStub.deleteTopupPermanent(com.sanedge.paymentgateway.pb.topup.FindByIdTopupRequest.newBuilder()
                .setTopupId(id.intValue())
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
        var resp = topupCommandStub.restoreAllTopup(Empty.getDefaultInstance());
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(success(resp.getStatus()))
                .build());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        var resp = topupCommandStub.deleteAllTopupPermanent(Empty.getDefaultInstance());
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
