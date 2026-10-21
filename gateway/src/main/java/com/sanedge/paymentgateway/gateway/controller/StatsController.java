package com.sanedge.paymentgateway.gateway.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.paymentgateway.common.domain.responses.api.ApiResponse;
import com.sanedge.paymentgateway.gateway.dto.responses.stats.StatItemResponse;
import com.sanedge.paymentgateway.gateway.mapper.ProtoMapper;
import com.sanedge.paymentgateway.pb.stats.StatsReaderServiceGrpc;
import com.sanedge.paymentgateway.pb.stats.StatsRequest;

import lombok.RequiredArgsConstructor;

/**
 * REST facade over the ClickHouse-backed {@code stats-reader} gRPC service.
 * All analytics previously served per-domain are now read from here.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsReaderServiceGrpc.StatsReaderServiceBlockingStub statsReaderStub;

    @GetMapping("/monthly-total-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findMonthlyTotalRevenue(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        var resp = statsReaderStub.findMonthlyTotalRevenue(StatsRequest.newBuilder()
                .setYear(nvl(year))
                .setMonth(nvl(month))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    @GetMapping("/card-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findCardStats(
            @RequestParam(required = false) Long cardId,
            @RequestParam(required = false) Integer year) {
        var resp = statsReaderStub.findCardStats(StatsRequest.newBuilder()
                .setCardId(nvl(cardId))
                .setYear(nvl(year))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    @GetMapping("/yearly-volume")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findYearlyVolume(
            @RequestParam(required = false) Integer year) {
        var resp = statsReaderStub.findYearlyVolume(StatsRequest.newBuilder()
                .setYear(nvl(year))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    @GetMapping("/merchant-volume")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<StatItemResponse>>> findMerchantVolume(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer year) {
        var resp = statsReaderStub.findMerchantVolume(StatsRequest.newBuilder()
                .setMerchantId(nvl(merchantId))
                .setYear(nvl(year))
                .build());
        return ResponseEntity.ok(ApiResponse.<List<StatItemResponse>>builder()
                .status(resp.getStatus())
                .message(resp.getMessage())
                .data(ProtoMapper.statItems(resp.getDataList()))
                .build());
    }

    private static int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private static long nvl(Long value) {
        return value == null ? 0L : value;
    }
}
