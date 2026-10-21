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

import com.sanedge.payment_gateway.domain.requests.topup.CreateTopupRequest;
import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopups;
import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopupsByCardNumber;
import com.sanedge.payment_gateway.domain.requests.topup.UpdateTopupRequest;
import com.sanedge.payment_gateway.domain.requests.topup.stats.MonthTopupStatus;
import com.sanedge.payment_gateway.domain.requests.topup.stats.YearMonthMethod;
import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.MonthTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.YearTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupYearlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupYearlyMethodResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusSuccess;
import com.sanedge.payment_gateway.service.topup.TopupCommandService;
import com.sanedge.payment_gateway.service.topup.TopupQueryService;
import com.sanedge.payment_gateway.service.topup.stats.amount.TopupAmountByCardService;
import com.sanedge.payment_gateway.service.topup.stats.amount.TopupAmountService;
import com.sanedge.payment_gateway.service.topup.stats.method.TopupMethodByCardService;
import com.sanedge.payment_gateway.service.topup.stats.method.TopupMethodService;
import com.sanedge.payment_gateway.service.topup.stats.status.TopupStatusByCardService;
import com.sanedge.payment_gateway.service.topup.stats.status.TopupStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/topups")
public class TopupController {

    private final TopupQueryService topupQueryService;
    private final TopupCommandService topupCommandService;

    private final TopupAmountService topupAmountService;
    private final TopupMethodService topupMethodService;
    private final TopupStatusService topupStatusService;

    private final TopupAmountByCardService topupAmountByCardService;
    private final TopupMethodByCardService topupMethodByCardService;
    private final TopupStatusByCardService topupStatusByCardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponse>>> findAll(
            @ModelAttribute FindAllTopups req) {
        return ResponseEntity.ok(topupQueryService.findAll(req));
    }

    @GetMapping("/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponse>>> findAllByCardNumber(
            @ModelAttribute FindAllTopupsByCardNumber req) {
        return ResponseEntity.ok(topupQueryService.findAllByCardNumber(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllTopups req) {
        return ResponseEntity.ok(topupQueryService.findActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TopupResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllTopups req) {
        return ResponseEntity.ok(topupQueryService.findTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<TopupResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(topupQueryService.findById(id));
    }

    @GetMapping("/card/{cardNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<List<TopupResponse>>> findByCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(topupQueryService.findByCard(cardNumber));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TopupResponse>> create(@RequestBody CreateTopupRequest req) {
        return ResponseEntity.ok(topupCommandService.create(req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TopupResponse>> update(@RequestBody UpdateTopupRequest req) {
        return ResponseEntity.ok(topupCommandService.update(req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopupResponseDeleteAt>> trash(@PathVariable Long id) {
        return ResponseEntity.ok(topupCommandService.trashed(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TopupResponseDeleteAt>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(topupCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        return ResponseEntity.ok(topupCommandService.deletePermanent(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(topupCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(topupCommandService.deleteAll());
    }

    @GetMapping("/stats/amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupMonthAmountResponse>>> getMonthlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(topupAmountService.findMonthlyAmounts(year));
    }

    @GetMapping("/stats/amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupYearlyAmountResponse>>> getYearlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(topupAmountService.findYearlyAmounts(year));
    }

    @GetMapping("/stats/amount/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupMonthAmountResponse>>> getMonthlyAmountsByCard(
            @ModelAttribute YearMonthMethod req) {
        return ResponseEntity.ok(topupAmountByCardService.findMonthlyAmounts(req));
    }

    @GetMapping("/stats/amount/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupYearlyAmountResponse>>> getYearlyAmountsByCard(
            @ModelAttribute YearMonthMethod req) {
        return ResponseEntity.ok(topupAmountByCardService.findYearlyAmounts(req));
    }

    @GetMapping("/stats/method/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupMonthMethodResponse>>> getMonthlyMethods(
            @RequestParam Long year) {
        return ResponseEntity.ok(topupMethodService.findMonthlyMethods(year));
    }

    @GetMapping("/stats/method/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupYearlyMethodResponse>>> getYearlyMethods(
            @RequestParam Long year) {
        return ResponseEntity.ok(topupMethodService.findYearlyMethods(year));
    }

    @GetMapping("/stats/method/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupMonthMethodResponse>>> getMonthlyMethodsByCard(
            @ModelAttribute YearMonthMethod req) {
        return ResponseEntity.ok(topupMethodByCardService.findMonthlyMethods(req));
    }

    @GetMapping("/stats/method/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupYearlyMethodResponse>>> getYearlyMethodsByCard(
            @ModelAttribute YearMonthMethod req) {
        return ResponseEntity.ok(topupMethodByCardService.findYearlyMethods(req));
    }

    @GetMapping("/stats/status/monthly/success")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseMonthStatusSuccess>>> getMonthlyStatusSuccess(
            @ModelAttribute MonthTopupStatus req) {
        return ResponseEntity.ok(topupStatusService.findMonthStatusSuccess(req));
    }

    @GetMapping("/stats/status/yearly/success")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseYearStatusSuccess>>> getYearlyStatusSuccess(
            @RequestParam Long year) {
        return ResponseEntity.ok(topupStatusService.findYearlyStatusSuccess(year));
    }

    @GetMapping("/stats/status/monthly/failed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseMonthStatusFailed>>> getMonthlyStatusFailed(
            @ModelAttribute MonthTopupStatus req) {
        return ResponseEntity.ok(topupStatusService.findMonthStatusFailed(req));
    }

    @GetMapping("/stats/status/yearly/failed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseYearStatusFailed>>> getYearlyStatusFailed(
            @RequestParam Long year) {
        return ResponseEntity.ok(topupStatusService.findYearlyStatusFailed(year));
    }

    @GetMapping("/stats/status/monthly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseMonthStatusSuccess>>> getMonthlyStatusSuccessByCard(
            @ModelAttribute MonthTopupStatusCardNumber req) {
        return ResponseEntity.ok(topupStatusByCardService.findMonthStatusSuccess(req));
    }

    @GetMapping("/stats/status/yearly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseYearStatusSuccess>>> getYearlyStatusSuccessByCard(
            @ModelAttribute YearTopupStatusCardNumber req) {
        return ResponseEntity.ok(topupStatusByCardService.findYearlyStatusSuccess(req));
    }

    @GetMapping("/stats/status/monthly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseMonthStatusFailed>>> getMonthlyStatusFailedByCard(
            @ModelAttribute MonthTopupStatusCardNumber req) {
        return ResponseEntity.ok(topupStatusByCardService.findMonthStatusFailed(req));
    }

    @GetMapping("/stats/status/yearly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<TopupResponseYearStatusFailed>>> getYearlyStatusFailedByCard(
            @ModelAttribute YearTopupStatusCardNumber req) {
        return ResponseEntity.ok(topupStatusByCardService.findYearlyStatusFailed(req));
    }
}
