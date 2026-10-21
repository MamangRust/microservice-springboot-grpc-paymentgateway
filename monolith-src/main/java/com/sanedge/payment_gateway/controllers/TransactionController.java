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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.payment_gateway.domain.requests.transaction.CreateTransactionRequest;
import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactions;
import com.sanedge.payment_gateway.domain.requests.transaction.UpdateTransactionRequest;
import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthStatusTransaction;
import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthYearPaymentMethod;
import com.sanedge.payment_gateway.domain.requests.transaction.statsbycard.MonthStatusTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.statsbycard.YearStatusTransactionCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionYearMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusSuccess;
import com.sanedge.payment_gateway.service.transaction.TransactionCommandService;
import com.sanedge.payment_gateway.service.transaction.TransactionQueryService;
import com.sanedge.payment_gateway.service.transaction.stats.amount.TransactionAmountByCardService;
import com.sanedge.payment_gateway.service.transaction.stats.amount.TransactionAmountService;
import com.sanedge.payment_gateway.service.transaction.stats.method.TransactionMethodByCardService;
import com.sanedge.payment_gateway.service.transaction.stats.method.TransactionMethodService;
import com.sanedge.payment_gateway.service.transaction.stats.status.TransactionStatusByCardService;
import com.sanedge.payment_gateway.service.transaction.stats.status.TransactionStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionQueryService transactionQueryService;
    private final TransactionCommandService transactionCommandService;

    private final TransactionAmountService transactionAmountService;
    private final TransactionMethodService transactionMethodService;
    private final TransactionStatusService transactionStatusService;

    private final TransactionAmountByCardService transactionAmountByCardService;
    private final TransactionMethodByCardService transactionMethodByCardService;
    private final TransactionStatusByCardService transactionStatusByCardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findAll(
            @ModelAttribute FindAllTransactions req) {
        return ResponseEntity.ok(transactionQueryService.findAll(req));
    }

    @GetMapping("/by-card")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponse>>> findAllByCardNumber(
            @ModelAttribute FindAllTransactionCardNumber req) {
        return ResponseEntity.ok(transactionQueryService.findAllByCardNumber(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllTransactions req) {
        return ResponseEntity.ok(transactionQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransactionResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllTransactions req) {
        return ResponseEntity.ok(transactionQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionQueryService.findById(id));
    }

    @GetMapping("/merchant/{merchantId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findByMerchantId(
            @PathVariable Long merchantId) {
        return ResponseEntity.ok(transactionQueryService.findByMerchantId(merchantId));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> create(
            @RequestHeader("X-api-Key") String apiKey,
            @RequestBody CreateTransactionRequest req) {
        return ResponseEntity.ok(transactionCommandService.create(apiKey, req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF','USER')")
    public ResponseEntity<ApiResponse<TransactionResponse>> update(
            @RequestHeader("X-api-Key") String apiKey,
            @RequestBody UpdateTransactionRequest req) {
        return ResponseEntity.ok(transactionCommandService.update(apiKey, req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> trash(@PathVariable Long id) {
        return ResponseEntity.ok(transactionCommandService.trashed(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponseDeleteAt>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(transactionCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        return ResponseEntity.ok(transactionCommandService.deletePermanent(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(transactionCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(transactionCommandService.deleteAll());
    }

    @GetMapping("/stats/amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthAmountResponse>>> findMonthlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(transactionAmountService.findMonthlyAmounts(year));
    }

    @GetMapping("/stats/amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyAmountResponse>>> findYearlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(transactionAmountService.findYearlyAmounts(year));
    }

    @GetMapping("/stats/amount/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthAmountResponse>>> findMonthlyAmountsByCard(
            @ModelAttribute MonthYearPaymentMethod req) {
        return ResponseEntity.ok(transactionAmountByCardService.findMonthlyAmounts(req));
    }

    @GetMapping("/stats/amount/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearlyAmountResponse>>> findYearlyAmountsByCard(
            @ModelAttribute MonthYearPaymentMethod req) {
        return ResponseEntity.ok(transactionAmountByCardService.findYearlyAmounts(req));
    }

    @GetMapping("/stats/method/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthMethodResponse>>> findMonthlyMethods(
            @RequestParam Long year) {
        return ResponseEntity.ok(transactionMethodService.findMonthlyMethod(year));
    }

    @GetMapping("/stats/method/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearMethodResponse>>> findYearlyMethods(
            @RequestParam Long year) {
        return ResponseEntity.ok(transactionMethodService.findYearlyMethod(year));
    }

    @GetMapping("/stats/method/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionMonthMethodResponse>>> findMonthlyMethodsByCard(
            @ModelAttribute MonthYearPaymentMethod req) {
        return ResponseEntity.ok(transactionMethodByCardService.findMonthlyMethod(req));
    }

    @GetMapping("/stats/method/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionYearMethodResponse>>> findYearlyMethodsByCard(
            @ModelAttribute MonthYearPaymentMethod req) {
        return ResponseEntity.ok(transactionMethodByCardService.findYearlyMethod(req));
    }

    @GetMapping("/stats/status/monthly/success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseMonthStatusSuccess>>> findMonthlyStatusSuccess(
            @ModelAttribute MonthStatusTransaction req) {
        return ResponseEntity.ok(transactionStatusService.findMonthStatusSuccess(req));
    }

    @GetMapping("/stats/status/yearly/success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseYearStatusSuccess>>> findYearlyStatusSuccess(
            @RequestParam Long year) {
        return ResponseEntity.ok(transactionStatusService.findYearlyStatusSuccess(year));
    }

    @GetMapping("/stats/status/monthly/failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseMonthStatusFailed>>> findMonthlyStatusFailed(
            @ModelAttribute MonthStatusTransaction req) {
        return ResponseEntity.ok(transactionStatusService.findMonthStatusFailed(req));
    }

    @GetMapping("/stats/status/yearly/failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseYearStatusFailed>>> findYearlyStatusFailed(
            @RequestParam Long year) {
        return ResponseEntity.ok(transactionStatusService.findYearlyStatusFailed(year));
    }

    @GetMapping("/stats/status/monthly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseMonthStatusSuccess>>> findMonthlyStatusSuccessByCard(
            @ModelAttribute MonthStatusTransactionCardNumber req) {
        return ResponseEntity.ok(transactionStatusByCardService.findMonthStatusSuccess(req));
    }

    @GetMapping("/stats/status/yearly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseYearStatusSuccess>>> findYearlyStatusSuccessByCard(
            @ModelAttribute YearStatusTransactionCardNumber req) {
        return ResponseEntity.ok(transactionStatusByCardService.findYearlyStatusSuccess(req));
    }

    @GetMapping("/stats/status/monthly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseMonthStatusFailed>>> findMonthlyStatusFailedByCard(
            @ModelAttribute MonthStatusTransactionCardNumber req) {
        return ResponseEntity.ok(transactionStatusByCardService.findMonthStatusFailed(req));
    }

    @GetMapping("/stats/status/yearly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransactionResponseYearStatusFailed>>> findYearlyStatusFailedByCard(
            @ModelAttribute YearStatusTransactionCardNumber req) {
        return ResponseEntity.ok(transactionStatusByCardService.findYearlyStatusFailed(req));
    }
}
