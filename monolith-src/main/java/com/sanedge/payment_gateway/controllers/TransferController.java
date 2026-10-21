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

import com.sanedge.payment_gateway.domain.requests.transfers.CreateTransferRequest;
import com.sanedge.payment_gateway.domain.requests.transfers.FindAllTransfers;
import com.sanedge.payment_gateway.domain.requests.transfers.UpdateTransferRequest;
import com.sanedge.payment_gateway.domain.requests.transfers.stats.MonthStatusTransfer;
import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.MonthStatusTransferCardNumber;
import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.MonthYearCardNumber;
import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.YearStatusTransferCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferYearAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusSuccess;
import com.sanedge.payment_gateway.service.transfer.TransferCommandService;
import com.sanedge.payment_gateway.service.transfer.TransferQueryService;
import com.sanedge.payment_gateway.service.transfer.stats.amount.TransferAmountByCardService;
import com.sanedge.payment_gateway.service.transfer.stats.amount.TransferAmountService;
import com.sanedge.payment_gateway.service.transfer.stats.status.TransferStatusByCardService;
import com.sanedge.payment_gateway.service.transfer.stats.status.TransferStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferQueryService transferQueryService;
    private final TransferCommandService transferCommandService;
    private final TransferAmountService transferAmountService;
    private final TransferStatusService transferStatusService;
    private final TransferAmountByCardService transferAmountByCardService;
    private final TransferStatusByCardService transferStatusByCardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransferResponse>>> findAll(
            @ModelAttribute FindAllTransfers req) {
        return ResponseEntity.ok(transferQueryService.findAll(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(transferQueryService.findById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransferResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllTransfers req) {
        return ResponseEntity.ok(transferQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponsePagination<List<TransferResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllTransfers req) {
        return ResponseEntity.ok(transferQueryService.findByTrashed(req));
    }

    @GetMapping("/from/{transferFrom}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> findByTransferFrom(
            @PathVariable String transferFrom) {
        return ResponseEntity.ok(transferQueryService.findByTransferFrom(transferFrom));
    }

    @GetMapping("/to/{transferTo}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> findByTransferTo(
            @PathVariable String transferTo) {
        return ResponseEntity.ok(transferQueryService.findByTransferTo(transferTo));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF', 'USER')")
    public ResponseEntity<ApiResponse<TransferResponse>> create(@RequestBody CreateTransferRequest req) {
        return ResponseEntity.ok(transferCommandService.create(req));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponse>> update(@RequestBody UpdateTransferRequest req) {
        return ResponseEntity.ok(transferCommandService.update(req));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponseDeleteAt>> trash(@PathVariable Long id) {
        return ResponseEntity.ok(transferCommandService.trashed(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<TransferResponseDeleteAt>> restore(@PathVariable Long id) {
        return ResponseEntity.ok(transferCommandService.restore(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Long id) {
        return ResponseEntity.ok(transferCommandService.deletePermanent(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(transferCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(transferCommandService.deleteAll());
    }

    @GetMapping("/stats/amount/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferMonthAmountResponse>>> findMonthlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(transferAmountService.findMonthlyAmounts(year));
    }

    @GetMapping("/stats/amount/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferYearAmountResponse>>> findYearlyAmounts(
            @RequestParam Long year) {
        return ResponseEntity.ok(transferAmountService.findYearlyAmounts(year));
    }

    @GetMapping("/stats/amount/monthly/by-card/sender")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferMonthAmountResponse>>> findMonthlyAmountsBySender(
            @ModelAttribute MonthYearCardNumber req) {
        return ResponseEntity.ok(transferAmountByCardService.findMonthlyAmountsBySender(req));
    }

    @GetMapping("/stats/amount/monthly/by-card/receiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferMonthAmountResponse>>> findMonthlyAmountsByReceiver(
            @ModelAttribute MonthYearCardNumber req) {
        return ResponseEntity.ok(transferAmountByCardService.findMonthlyAmountsByReceiver(req));
    }

    @GetMapping("/stats/amount/yearly/by-card/sender")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferYearAmountResponse>>> findYearlyAmountsBySender(
            @ModelAttribute MonthYearCardNumber req) {
        return ResponseEntity.ok(transferAmountByCardService.findYearlyAmountsBySender(req));
    }

    @GetMapping("/stats/amount/yearly/by-card/receiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferYearAmountResponse>>> findYearlyAmountsByReceiver(
            @ModelAttribute MonthYearCardNumber req) {
        return ResponseEntity.ok(transferAmountByCardService.findYearlyAmountsByReceiver(req));
    }

    @GetMapping("/stats/status/monthly/success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseMonthStatusSuccess>>> findMonthlyStatusSuccess(
            @ModelAttribute MonthStatusTransfer req) {
        return ResponseEntity.ok(transferStatusService.findMonthStatusSuccess(req));
    }

    @GetMapping("/stats/status/yearly/success")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseYearStatusSuccess>>> findYearlyStatusSuccess(
            @RequestParam Long year) {
        return ResponseEntity.ok(transferStatusService.findYearlyStatusSuccess(year));
    }

    @GetMapping("/stats/status/monthly/failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseMonthStatusFailed>>> findMonthlyStatusFailed(
            @ModelAttribute MonthStatusTransfer req) {
        return ResponseEntity.ok(transferStatusService.findMonthStatusFailed(req));
    }

    @GetMapping("/stats/status/yearly/failed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseYearStatusFailed>>> findYearlyStatusFailed(
            @RequestParam Long year) {
        return ResponseEntity.ok(transferStatusService.findYearlyStatusFailed(year));
    }

    @GetMapping("/stats/status/monthly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseMonthStatusSuccess>>> findMonthlyStatusSuccessByCard(
            @ModelAttribute MonthStatusTransferCardNumber req) {
        return ResponseEntity.ok(transferStatusByCardService.findMonthStatusSuccessByCard(req));
    }

    @GetMapping("/stats/status/yearly/success/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseYearStatusSuccess>>> findYearlyStatusSuccessByCard(
            @ModelAttribute YearStatusTransferCardNumber req) {
        return ResponseEntity.ok(transferStatusByCardService.findYearlyStatusSuccessByCard(req));
    }

    @GetMapping("/stats/status/monthly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseMonthStatusFailed>>> findMonthlyStatusFailedByCard(
            @ModelAttribute MonthStatusTransferCardNumber req) {
        return ResponseEntity.ok(transferStatusByCardService.findMonthStatusFailedByCard(req));
    }

    @GetMapping("/stats/status/yearly/failed/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<TransferResponseYearStatusFailed>>> findYearlyStatusFailedByCard(
            @ModelAttribute YearStatusTransferCardNumber req) {
        return ResponseEntity.ok(transferStatusByCardService.findYearlyStatusFailedByCard(req));
    }
}
