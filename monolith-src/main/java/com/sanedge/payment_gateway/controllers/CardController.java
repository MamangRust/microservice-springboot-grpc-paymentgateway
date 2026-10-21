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

import com.sanedge.payment_gateway.domain.requests.card.CreateCardRequest;
import com.sanedge.payment_gateway.domain.requests.card.FindAllCards;
import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.requests.card.UpdateCardRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponseDeleteAt;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboard;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboardCard;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseYearBalance;
import com.sanedge.payment_gateway.service.card.CardCommandService;
import com.sanedge.payment_gateway.service.card.CardDashboardService;
import com.sanedge.payment_gateway.service.card.CardQueryService;
import com.sanedge.payment_gateway.service.card.stats.balance.CardBalanceByCardService;
import com.sanedge.payment_gateway.service.card.stats.balance.CardBalanceService;
import com.sanedge.payment_gateway.service.card.stats.topup.CardTopupAmountByCardService;
import com.sanedge.payment_gateway.service.card.stats.topup.CardTopupAmountService;
import com.sanedge.payment_gateway.service.card.stats.transaction.CardTransactionAmountByCardService;
import com.sanedge.payment_gateway.service.card.stats.transaction.CardTransactionAmountService;
import com.sanedge.payment_gateway.service.card.stats.transfer.CardTransferAmountByCardService;
import com.sanedge.payment_gateway.service.card.stats.transfer.CardTransferAmountService;
import com.sanedge.payment_gateway.service.card.stats.withdraw.CardWithdrawAmountByCardService;
import com.sanedge.payment_gateway.service.card.stats.withdraw.CardWithdrawAmountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
@PreAuthorize("isAuthenticated()")
public class CardController {
    private final CardQueryService cardQueryService;
    private final CardCommandService cardCommandService;
    private final CardBalanceService cardBalanceService;
    private final CardTopupAmountService cardTopupAmountService;
    private final CardTransactionAmountService cardTransactionAmountService;
    private final CardTransferAmountService cardTransferAmountService;
    private final CardWithdrawAmountService cardWithdrawAmountService;
    private final CardDashboardService cardDashboardService;
    private final CardBalanceByCardService cardBalanceByCardService;
    private final CardTopupAmountByCardService cardTopupAmountByCardService;
    private final CardTransactionAmountByCardService cardTransactionAmountByCardService;
    private final CardTransferAmountByCardService cardTransferAmountByCardService;
    private final CardWithdrawAmountByCardService cardWithdrawAmountByCardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<CardResponse>>> findCards(
            @Valid @ModelAttribute FindAllCards req) {
        return ResponseEntity.ok(cardQueryService.findAll(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<CardResponseDeleteAt>>> findActiveCards(
            @Valid @ModelAttribute FindAllCards req) {
        return ResponseEntity.ok(cardQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<CardResponseDeleteAt>>> findTrashedCards(
            @Valid @ModelAttribute FindAllCards req) {
        return ResponseEntity.ok(cardQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardResponse>> findCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardQueryService.findById(id));
    }

    @GetMapping("/by-user/{user_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardResponse>> findCardByUser(@PathVariable Long user_id) {
        return ResponseEntity.ok(cardQueryService.findByUserId(user_id));
    }

    @GetMapping("/by-number/{card_number}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardResponse>> findCardByNumber(@PathVariable String card_number) {
        return ResponseEntity.ok(cardQueryService.findByCardNumber(card_number));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponse>> createCard(
            @Valid @RequestBody CreateCardRequest request) {
        return ResponseEntity.ok(cardCommandService.createCard(request));
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponse>> updateCard(
            @Valid @RequestBody UpdateCardRequest request) {
        return ResponseEntity.ok(cardCommandService.updateCard(request));
    }

    @PostMapping("/trash/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponseDeleteAt>> trashCard(
            @PathVariable Long id) {
        return ResponseEntity.ok(cardCommandService.trashCard(id)); // CardCommandService might still take int
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CardResponseDeleteAt>> restoreCard(
            @PathVariable Long id) {
        return ResponseEntity.ok(cardCommandService.restoreCard(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardCommandService.deleteCard(id));
    }

    @PostMapping("/restore-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllCards() {
        return ResponseEntity.ok(cardCommandService.restoreAll());
    }

    @PostMapping("/delete-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCards() {
        return ResponseEntity.ok(cardCommandService.deleteAll());
    }

    @GetMapping("/stats/balance/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthBalance>>> findMonthlyBalance(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardBalanceService.findMonthBalance(year));
    }

    @GetMapping("/stats/balance/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseYearBalance>>> findYearlyBalance(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardBalanceService.findYearBalance(year));
    }

    @GetMapping("/stats/topup/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> findMonthlyTopupAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTopupAmountService.findMonthAmount(year));
    }

    @GetMapping("/stats/topup/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> findYearlyTopupAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTopupAmountService.findYearAmount(year));
    }

    @GetMapping("/stats/transaction/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> findMonthlyTransactionAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTransactionAmountService.findMonthAmount(year));
    }

    @GetMapping("/stats/transaction/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> findYearlyTransactionAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTransactionAmountService.findYearAmount(year));
    }

    @GetMapping("/stats/transfer/monthly/sender")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> findMonthlyTransferAmountSender(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTransferAmountService.findMonthAmountSender(year));
    }

    @GetMapping("/stats/transfer/monthly/receiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> findMonthlyTransferAmountReceiver(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTransferAmountService.findMonthAmountReceiver(year));
    }

    @GetMapping("/stats/transfer/yearly/sender")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> findYearlyTransferAmountSender(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTransferAmountService.findYearAmountSender(year));
    }

    @GetMapping("/stats/transfer/yearly/receiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> findYearlyTransferAmountReceiver(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardTransferAmountService.findYearAmountReceiver(year));
    }

    @GetMapping("/stats/withdraw/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> findMonthlyWithdrawAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardWithdrawAmountService.findMonthAmount(year));
    }

    @GetMapping("/stats/withdraw/yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> findYearlyWithdrawAmount(
            @RequestParam Long year) {
        return ResponseEntity.ok(cardWithdrawAmountService.findYearAmount(year));
    }

    @GetMapping("/stats/balance/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthBalance>>> getMonthlyBalanceByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardBalanceByCardService.findMonthBalanceByCard(req));
    }

    @GetMapping("/stats/balance/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseYearBalance>>> getYearlyBalanceByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardBalanceByCardService.findYearBalanceByCard(req));
    }

    @GetMapping("/stats/topup/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> getMonthlyTopupAmountByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTopupAmountByCardService.findMonthAmountByCard(req));
    }

    @GetMapping("/stats/topup/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> getYearlyTopupAmountByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTopupAmountByCardService.findYearAmountByCard(req));
    }

    @GetMapping("/stats/transaction/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> getMonthlyTransactionAmountByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTransactionAmountByCardService.findMonthAmountByCard(req));
    }

    @GetMapping("/stats/transaction/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> getYearlyTransactionAmountByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTransactionAmountByCardService.findYearAmountByCard(req));
    }

    @GetMapping("/stats/transfer/monthly/by-card/sender")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> getMonthlyTransferAmountByCardSender(
            @RequestParam Long year,
            @RequestParam(required = false) Integer month,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTransferAmountByCardService.findMonthAmountSender(req));
    }

    @GetMapping("/stats/transfer/monthly/by-card/receiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> getMonthlyTransferAmountByCardReceiver(
            @RequestParam Long year,
            @RequestParam(required = false) Integer month,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTransferAmountByCardService.findMonthAmountReceiver(req));
    }

    @GetMapping("/stats/transfer/yearly/by-card/sender")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> getYearlyTransferAmountByCardSender(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTransferAmountByCardService.findYearAmountSender(req));
    }

    @GetMapping("/stats/transfer/yearly/by-card/receiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> getYearlyTransferAmountByCardReceiver(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardTransferAmountByCardService.findYearAmountReceiver(req));
    }

    @GetMapping("/stats/withdraw/monthly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseMonthAmount>>> getMonthlyWithdrawAmountByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardWithdrawAmountByCardService.findMonthAmountByCard(req));
    }

    @GetMapping("/stats/withdraw/yearly/by-card")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<List<CardResponseYearAmount>>> getYearlyWithdrawAmountByCard(
            @RequestParam Long year,
            @RequestParam String cardNumber) {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(cardNumber, year);
        return ResponseEntity.ok(cardWithdrawAmountByCardService.findYearAmountByCard(req));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<CardDashboard>> findCardDashboard() {
        ApiResponse<CardDashboard> response = cardDashboardService.dashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/{card_number}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<CardDashboardCard>> findCardDashboardByCardNumber(
            @PathVariable String card_number) {
        ApiResponse<CardDashboardCard> response = cardDashboardService.dashboardByCard(card_number);
        return ResponseEntity.ok(response);
    }
}