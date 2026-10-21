package com.sanedge.payment_gateway.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.payment_gateway.domain.requests.withdraws.CreateWithdrawRequest;
import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdraws;
import com.sanedge.payment_gateway.domain.requests.withdraws.MonthStatusWithdraw;
import com.sanedge.payment_gateway.domain.requests.withdraws.UpdateWithdrawRequest;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.MonthStatusWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearMonthCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearStatusWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;
import com.sanedge.payment_gateway.service.withdraw.WithdrawCommandService;
import com.sanedge.payment_gateway.service.withdraw.WithdrawQueryService;
import com.sanedge.payment_gateway.service.withdraw.stats.amount.WithdrawAmountByCardService;
import com.sanedge.payment_gateway.service.withdraw.stats.amount.WithdrawAmountService;
import com.sanedge.payment_gateway.service.withdraw.stats.status.WithdrawStatusByCardService;
import com.sanedge.payment_gateway.service.withdraw.stats.status.WithdrawStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/withdraws")
public class WithdrawController {

    private final WithdrawQueryService withdrawQueryService;
    private final WithdrawCommandService withdrawCommandService;

    private final WithdrawAmountService withdrawAmountService;
    private final WithdrawStatusService withdrawStatusService;

    private final WithdrawAmountByCardService withdrawAmountByCardService;
    private final WithdrawStatusByCardService withdrawStatusByCardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<WithdrawResponse>>> findAll(
            @ModelAttribute FindAllWithdraws req) {
        return ResponseEntity.ok(withdrawQueryService.findAll(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<WithdrawResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(withdrawQueryService.findById(id));
    }

    @GetMapping("/by-card/{cardNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<WithdrawResponse>>> findByCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(withdrawQueryService.findByCard(cardNumber));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<WithdrawResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllWithdraws req) {
        return ResponseEntity.ok(withdrawQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<WithdrawResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllWithdraws req) {
        return ResponseEntity.ok(withdrawQueryService.findByTrashed(req));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<WithdrawResponse>> create(@RequestBody CreateWithdrawRequest req) {
        return ResponseEntity.ok(withdrawCommandService.create(req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<WithdrawResponse>> update(@RequestBody UpdateWithdrawRequest req) {
        return ResponseEntity.ok(withdrawCommandService.update(req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<WithdrawResponseDeleteAt>> trash(@PathVariable Long id) {
        return ResponseEntity.ok(withdrawCommandService.trashed(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<WithdrawResponseDeleteAt>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(withdrawCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        return ResponseEntity.ok(withdrawCommandService.deletePermanent(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(withdrawCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(withdrawCommandService.deleteAll());
    }

    @GetMapping("/stats/amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawMonthlyAmountResponse>>> findMonthlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(withdrawAmountService.findMonthlyWithdraws(year));
    }

    @GetMapping("/stats/amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawYearlyAmountResponse>>> findYearlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(withdrawAmountService.findYearlyWithdraws(year));
    }

    @GetMapping("/stats/amount/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawMonthlyAmountResponse>>> findMonthlyByCard(
            @ModelAttribute YearMonthCardNumber req) {
        return ResponseEntity.ok(withdrawAmountByCardService.findMonthlyByCardNumber(req));
    }

    @GetMapping("/stats/amount/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawYearlyAmountResponse>>> findYearlyByCard(
            @ModelAttribute YearMonthCardNumber req) {
        return ResponseEntity.ok(withdrawAmountByCardService.findYearlyByCardNumber(req));
    }

    @GetMapping("/stats/status/monthly/success")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseMonthStatusSuccess>>> findMonthlyStatusSuccess(
            @ModelAttribute MonthStatusWithdraw req) {
        return ResponseEntity.ok(withdrawStatusService.findMonthStatusSuccess(req));
    }

    @GetMapping("/stats/status/yearly/success")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseYearStatusSuccess>>> findYearlyStatusSuccess(
            @RequestParam Long year) {
        return ResponseEntity.ok(withdrawStatusService.findYearlyStatusSuccess(year));
    }

    @GetMapping("/stats/status/monthly/failed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseMonthStatusFailed>>> findMonthlyStatusFailed(
            @ModelAttribute MonthStatusWithdraw req) {
        return ResponseEntity.ok(withdrawStatusService.findMonthStatusFailed(req));
    }

    @GetMapping("/stats/status/yearly/failed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseYearStatusFailed>>> findYearlyStatusFailed(
            @RequestParam Long year) {
        return ResponseEntity.ok(withdrawStatusService.findYearlyStatusFailed(year));
    }

    @GetMapping("/stats/status/monthly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseMonthStatusSuccess>>> findMonthlyStatusSuccessByCard(
            @ModelAttribute MonthStatusWithdrawCardNumber req) {
        return ResponseEntity.ok(withdrawStatusByCardService.findMonthStatusSuccessByCard(req));
    }

    @GetMapping("/stats/status/yearly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseYearStatusSuccess>>> findYearlyStatusSuccessByCard(
            @ModelAttribute YearStatusWithdrawCardNumber req) {
        return ResponseEntity.ok(withdrawStatusByCardService.findYearlyStatusSuccessByCard(req));
    }

    @GetMapping("/stats/status/monthly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseMonthStatusFailed>>> findMonthlyStatusFailedByCard(
            @ModelAttribute MonthStatusWithdrawCardNumber req) {
        return ResponseEntity.ok(withdrawStatusByCardService.findMonthStatusFailedByCard(req));
    }

    @GetMapping("/stats/status/yearly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<WithdrawResponseYearStatusFailed>>> findYearlyStatusFailedByCard(
            @ModelAttribute YearStatusWithdrawCardNumber req) {
        return ResponseEntity.ok(withdrawStatusByCardService.findYearlyStatusFailedByCard(req));
    }
}
