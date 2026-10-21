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

import com.sanedge.payment_gateway.domain.requests.saldo.CreateSaldoRequest;
import com.sanedge.payment_gateway.domain.requests.saldo.FindAllSaldos;
import com.sanedge.payment_gateway.domain.requests.saldo.MonthTotalSaldoBalance;
import com.sanedge.payment_gateway.domain.requests.saldo.UpdateSaldoRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoYearBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoYearTotalBalanceResponse;
import com.sanedge.payment_gateway.service.saldo.SaldoCommandService;
import com.sanedge.payment_gateway.service.saldo.SaldoQueryService;
import com.sanedge.payment_gateway.service.saldo.stats.SaldoBalanceService;
import com.sanedge.payment_gateway.service.saldo.stats.SaldoTotalAmountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saldos")
public class SaldoController {

    private final SaldoQueryService saldoQueryService;
    private final SaldoCommandService saldoCommandService;
    private final SaldoBalanceService saldoBalanceService;
    private final SaldoTotalAmountService saldoTotalAmountService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SaldoResponse>>> findAll(
            @ModelAttribute FindAllSaldos req) {
        return ResponseEntity.ok(saldoQueryService.findAll(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SaldoResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllSaldos req) {
        return ResponseEntity.ok(saldoQueryService.findActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<SaldoResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllSaldos req) {
        return ResponseEntity.ok(saldoQueryService.findTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<SaldoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(saldoQueryService.findById(id));
    }

    @GetMapping("/by-card/{cardNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<SaldoResponse>> findByCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(saldoQueryService.findByCard(cardNumber));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponse>> create(@RequestBody CreateSaldoRequest req) {
        return ResponseEntity.ok(saldoCommandService.create(req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponse>> update(@RequestBody UpdateSaldoRequest req) {
        return ResponseEntity.ok(saldoCommandService.update(req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponseDeleteAt>> trash(@PathVariable Long id) {
        return ResponseEntity.ok(saldoCommandService.trash(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaldoResponseDeleteAt>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(saldoCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(saldoCommandService.delete(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(saldoCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(saldoCommandService.deleteAll());
    }

    @GetMapping("/balance/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<SaldoMonthBalanceResponse>>> getMonthBalance(
            @RequestParam Long year) {
        return ResponseEntity.ok(saldoBalanceService.getMonthBalance(year));
    }

    @GetMapping("/balance/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<SaldoYearBalanceResponse>>> getYearBalance(
            @RequestParam Long year) {
        return ResponseEntity.ok(saldoBalanceService.getYearBalance(year));
    }

    @GetMapping("/total-amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<SaldoMonthTotalBalanceResponse>>> getMonthTotalBalance(
            @ModelAttribute MonthTotalSaldoBalance req) {
        return ResponseEntity.ok(saldoTotalAmountService.findMonthTotalBalance(req));
    }

    @GetMapping("/total-amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<SaldoYearTotalBalanceResponse>>> getYearTotalBalance(
            @RequestParam Long year) {
        return ResponseEntity.ok(saldoTotalAmountService.findYearTotalBalance(year));
    }
}
