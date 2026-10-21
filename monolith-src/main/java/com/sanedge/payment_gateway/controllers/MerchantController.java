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

import com.sanedge.payment_gateway.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants;
import com.sanedge.payment_gateway.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearAmountApiKey;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearPaymentMethodApiKey;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearTotalAmountApiKey;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearAmountMerchant;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearPaymentMethodMerchant;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearTotalAmountMerchant;
import com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsByApiKey;
import com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsById;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantTransactionResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseYearlyAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.total_amount.MerchantResponseYearlyTotalAmount;
import com.sanedge.payment_gateway.service.merchant.MerchantCommandService;
import com.sanedge.payment_gateway.service.merchant.MerchantQueryService;
import com.sanedge.payment_gateway.service.merchant.MerchantTransactionService;
import com.sanedge.payment_gateway.service.merchant.stats.amount.MerchantAmountByApiKeyService;
import com.sanedge.payment_gateway.service.merchant.stats.amount.MerchantAmountByIdService;
import com.sanedge.payment_gateway.service.merchant.stats.amount.MerchantAmountService;
import com.sanedge.payment_gateway.service.merchant.stats.method.MerchantMethodByApiKeyService;
import com.sanedge.payment_gateway.service.merchant.stats.method.MerchantMethodByIdService;
import com.sanedge.payment_gateway.service.merchant.stats.method.MerchantMethodService;
import com.sanedge.payment_gateway.service.merchant.stats.totalamount.MerchantTotalAmountByApiKeyService;
import com.sanedge.payment_gateway.service.merchant.stats.totalamount.MerchantTotalAmountByIdService;
import com.sanedge.payment_gateway.service.merchant.stats.totalamount.MerchantTotalAmountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchants")
public class MerchantController {
    private final MerchantQueryService merchantQueryService;
    private final MerchantCommandService merchantCommandService;
    private final MerchantTransactionService merchantTransactionService;

    private final MerchantAmountService merchantAmountService;
    private final MerchantMethodService merchantMethodService;
    private final MerchantTotalAmountService merchantTotalAmountService;

    private final MerchantAmountByIdService merchantAmountByIdService;
    private final MerchantMethodByIdService merchantMethodByIdService;
    private final MerchantTotalAmountByIdService merchantTotalAmountByIdService;

    private final MerchantAmountByApiKeyService merchantAmountByApiKeyService;
    private final MerchantMethodByApiKeyService merchantMethodByApiKeyService;
    private final MerchantTotalAmountByApiKeyService merchantTotalAmountByApiKeyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponse>>> findAllMerchants(
            @ModelAttribute FindAllMerchants req) {
        return ResponseEntity.ok(merchantQueryService.findAll(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponseDeleteAt>>> findActiveMerchants(
            @ModelAttribute FindAllMerchants req) {
        return ResponseEntity.ok(merchantQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponseDeleteAt>>> findTrashedMerchants(
            @ModelAttribute FindAllMerchants req) {
        return ResponseEntity.ok(merchantQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantResponse>> findMerchantById(
            @PathVariable Long id) {
        return ResponseEntity.ok(merchantQueryService.findById(id));
    }

    @GetMapping("/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<MerchantResponse>> findMerchantByApiKey(
            @RequestParam String apiKey) {
        return ResponseEntity.ok(merchantQueryService.findByApiKey(apiKey));
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<MerchantResponse>>> findMerchantByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(merchantQueryService.findByUserId(userId));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantRequest req) {
        return ResponseEntity.ok(merchantCommandService.createMerchant(req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponse>> updateMerchant(
            @Valid @RequestBody UpdateMerchantRequest req) {
        return ResponseEntity.ok(merchantCommandService.updateMerchant(req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponseDeleteAt>> trashMerchant(
            @PathVariable Long id) {
        return ResponseEntity.ok(merchantCommandService.trashMerchant(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantResponseDeleteAt>> restoreMerchant(
            @PathVariable Long id) {
        return ResponseEntity.ok(merchantCommandService.restoreMerchant(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteMerchant(@PathVariable Long id) {
        return ResponseEntity.ok(merchantCommandService.deleteMerchant(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllMerchants() {
        return ResponseEntity.ok(merchantCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllMerchants() {
        return ResponseEntity.ok(merchantCommandService.deleteAll());
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantTransactionResponse>>> findAllTransactions(
            @ModelAttribute FindAllMerchants req) {
        return ResponseEntity.ok(merchantTransactionService.findAll(req));
    }

    @GetMapping("/transactions/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantTransactionResponse>>> findTransactionsById(
            @ModelAttribute FindAllMerchantTransactionsById req) {
        return ResponseEntity.ok(merchantTransactionService.findById(req));
    }

    @GetMapping("/transactions/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantTransactionResponse>>> findTransactionsByApiKey(
            @ModelAttribute FindAllMerchantTransactionsByApiKey req) {
        return ResponseEntity.ok(merchantTransactionService.findByApiKey(req));
    }

    @GetMapping("/stats/amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyAmount>>> getMonthlyAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(merchantAmountService.findMonthAmount(year));
    }

    @GetMapping("/stats/amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyAmount>>> getYearlyAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(merchantAmountService.findYearAmount(year));
    }

    @GetMapping("/stats/amount/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyAmount>>> getMonthlyAmountById(
            @ModelAttribute MonthYearAmountMerchant req) {
        return ResponseEntity.ok(merchantAmountByIdService.findMonthAmountById(req));
    }

    @GetMapping("/stats/amount/yearly/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyAmount>>> getYearlyAmountById(
            @ModelAttribute MonthYearAmountMerchant req) {
        return ResponseEntity.ok(merchantAmountByIdService.findYearAmountById(req));
    }

    @GetMapping("/stats/amount/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyAmount>>> getMonthlyAmountByApiKey(
            @ModelAttribute MonthYearAmountApiKey req) {
        return ResponseEntity.ok(merchantAmountByApiKeyService.findMonthAmountByApiKey(req));
    }

    @GetMapping("/stats/amount/yearly/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyAmount>>> getYearlyAmountByApiKey(
            @ModelAttribute MonthYearAmountApiKey req) {
        return ResponseEntity.ok(merchantAmountByApiKeyService.findYearAmountByApiKey(req));
    }

    @GetMapping("/stats/method/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyPaymentMethod>>> getMonthlyMethod(
            @RequestParam Long year) {
        return ResponseEntity.ok(merchantMethodService.findMonthMethod(year));
    }

    @GetMapping("/stats/method/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyPaymentMethod>>> getYearlyMethod(
            @RequestParam Long year) {
        return ResponseEntity.ok(merchantMethodService.findYearMethod(year));
    }

    @GetMapping("/stats/method/monthly/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyPaymentMethod>>> getMonthlyMethodById(
            @ModelAttribute MonthYearPaymentMethodMerchant req) {
        return ResponseEntity.ok(merchantMethodByIdService.findMonthMethodById(req));
    }

    @GetMapping("/stats/method/yearly/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyPaymentMethod>>> getYearlyMethodById(
            @ModelAttribute MonthYearPaymentMethodMerchant req) {
        return ResponseEntity.ok(merchantMethodByIdService.findYearMethodById(req));
    }

    @GetMapping("/stats/method/monthly/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyPaymentMethod>>> getMonthlyMethodByApiKey(
            @ModelAttribute MonthYearPaymentMethodApiKey req) {
        return ResponseEntity.ok(merchantMethodByApiKeyService.findMonthMethod(req));
    }

    @GetMapping("/stats/method/yearly/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyPaymentMethod>>> getYearlyMethodByApiKey(
            @ModelAttribute MonthYearPaymentMethodApiKey req) {
        return ResponseEntity.ok(merchantMethodByApiKeyService.findYearMethod(req));
    }

    @GetMapping("/stats/total-amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyTotalAmount>>> getMonthlyTotalAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(merchantTotalAmountService.findMonthTotalAmount(year));
    }

    @GetMapping("/stats/total-amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyTotalAmount>>> getYearlyTotalAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(merchantTotalAmountService.findYearTotalAmount(year));
    }

    @GetMapping("/stats/total-amount/monthly/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyTotalAmount>>> getMonthlyTotalAmountById(
            @ModelAttribute MonthYearTotalAmountMerchant req) {
        return ResponseEntity.ok(merchantTotalAmountByIdService.findMonthTotalAmountById(req));
    }

    @GetMapping("/stats/total-amount/yearly/by-id")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyTotalAmount>>> getYearlyTotalAmountById(
            @ModelAttribute MonthYearTotalAmountMerchant req) {
        return ResponseEntity.ok(merchantTotalAmountByIdService.findYearTotalAmountById(req));
    }

    @GetMapping("/stats/total-amount/monthly/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseMonthlyTotalAmount>>> getMonthlyTotalAmountByApiKey(
            @ModelAttribute MonthYearTotalAmountApiKey req) {
        return ResponseEntity.ok(merchantTotalAmountByApiKeyService.findMonthTotalAmountByApiKey(req));
    }

    @GetMapping("/stats/total-amount/yearly/by-api-key")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MerchantResponseYearlyTotalAmount>>> getYearlyTotalAmountByApiKey(
            @ModelAttribute MonthYearTotalAmountApiKey req) {
        return ResponseEntity.ok(merchantTotalAmountByApiKeyService.findYearTotalAmountByApiKey(req));
    }
}
