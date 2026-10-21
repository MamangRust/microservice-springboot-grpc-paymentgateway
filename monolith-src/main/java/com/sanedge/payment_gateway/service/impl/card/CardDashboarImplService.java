package com.sanedge.payment_gateway.service.impl.card;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboard;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboardCard;
import com.sanedge.payment_gateway.repository.card.dashboard.CardDashboardByCardRepository;
import com.sanedge.payment_gateway.repository.card.dashboard.CardDashboardRepository;
import com.sanedge.payment_gateway.service.card.CardDashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardDashboarImplService implements CardDashboardService {
    private CardDashboardRepository cardDashboardRepository;
    private CardDashboardByCardRepository cardDashboardByCardRepository;

    @Override
    public ApiResponse<CardDashboard> dashboard() {
        try {
            log.info("📊 Fetching global dashboard statistics");

            Long totalBalance = cardDashboardRepository.getTotalBalance();
            Long totalTopup = cardDashboardRepository.getTotalTopupAmount();
            Long totalTransaction = cardDashboardRepository.getTotalTransactionAmount();
            Long totalTransfer = cardDashboardRepository.getTotalTransferAmount();
            Long totalWithdraw = cardDashboardRepository.getTotalWithdrawAmount();

            CardDashboard dashboard = CardDashboard.builder()
                    .totalBalance(totalBalance)
                    .totalTopup(totalTopup)
                    .totalTransaction(totalTransaction)
                    .totalTransfer(totalTransfer)
                    .totalWithdraw(totalWithdraw)
                    .build();

            log.info("✅ Global dashboard retrieved successfully");
            return ApiResponse.<CardDashboard>builder()
                    .status("success")
                    .message("Global dashboard retrieved successfully")
                    .data(dashboard)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch dashboard data", e);
            return ApiResponse.<CardDashboard>builder()
                    .status("success")
                    .message("Failed to fetch dashboard data: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<CardDashboardCard> dashboardByCard(String cardNumber) {
        try {
            log.info("💳📊 Fetching dashboard for card: {}", cardNumber);

            Long totalBalance = cardDashboardByCardRepository.getTotalBalanceByCard(cardNumber);
            Long totalTopup = cardDashboardByCardRepository.getTotalTopupAmountByCard(cardNumber);
            Long totalTransaction = cardDashboardByCardRepository.getTotalTransactionAmountByCard(cardNumber);
            Long totalTransferSend = cardDashboardByCardRepository.getTotalTransferAmountBySender(cardNumber);
            Long totalTransferReceiver = cardDashboardByCardRepository.getTotalTransferAmountByReceiver(cardNumber);
            Long totalWithdraw = cardDashboardByCardRepository.getTotalWithdrawAmountByCard(cardNumber);

            CardDashboardCard dashboardCard = CardDashboardCard.builder()
                    .totalBalance(totalBalance)
                    .totalTopup(totalTopup)
                    .totalTransaction(totalTransaction)
                    .totalTransferSend(totalTransferSend)
                    .totalTransferReceiver(totalTransferReceiver)
                    .totalWithdraw(totalWithdraw)
                    .build();

            log.info("✅ Dashboard for card {} retrieved successfully", cardNumber);
            return ApiResponse.<CardDashboardCard>builder()
                    .status("success")
                    .message("Dashboard for card " + cardNumber + " retrieved successfully")
                    .data(dashboardCard)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to fetch dashboard for card {}", cardNumber, e);
            return ApiResponse.<CardDashboardCard>builder()
                    .status("success")
                    .message("Failed to fetch dashboard for card " + cardNumber + ": " + e.getMessage())
                    .build();
        }
    }
}