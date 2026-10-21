package com.sanedge.payment_gateway.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;

@AutoConfigureMockMvc
public class CardControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    private Long cardId;

    @BeforeEach
    void setUp() {
        cardId = adminCard.getCardId();
    }

    @Test
    void shouldFindAllCards() throws Exception {
        mockMvc.perform(get("/api/cards").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindActiveCards() throws Exception {
        mockMvc.perform(get("/api/cards/active").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindTrashedCards() throws Exception {
        mockMvc.perform(get("/api/cards/trashed").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindCardById() throws Exception {
        mockMvc.perform(get("/api/cards/" + cardId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindCardByUser() throws Exception {
        mockMvc.perform(get("/api/cards/by-user/" + adminUser.getUserId()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindCardByNumber() throws Exception {
        mockMvc.perform(get("/api/cards/by-number/" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateCard() throws Exception {
        String json = String.format("{\"userId\": %d, \"cardNumber\": \"7777666677778888\", \"cardType\": \"DEBIT\", \"cardProvider\": \"VISA\", \"cvv\": \"123\", \"expireDate\": \"2025-12-31\"}", adminUser.getUserId());
        mockMvc.perform(post("/api/cards/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateCard() throws Exception {
        String json = String.format("{\"cardId\": %d, \"userId\": %d, \"cardType\": \"CREDIT\", \"cardProvider\": \"MASTERCARD\", \"cvv\": \"456\", \"expireDate\": \"2026-12-31\"}", cardId, adminUser.getUserId());
        mockMvc.perform(post("/api/cards/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashCard() throws Exception {
        mockMvc.perform(post("/api/cards/trash/" + cardId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldRestoreCard() throws Exception {
        cardCommandRepository.trashed(cardId);
        mockMvc.perform(post("/api/cards/restore/" + cardId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteCard() throws Exception {
        mockMvc.perform(delete("/api/cards/" + cardId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldRestoreAll() throws Exception {
        mockMvc.perform(post("/api/cards/restore-all").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteAll() throws Exception {
        mockMvc.perform(post("/api/cards/delete-all").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindMonthlyBalance() throws Exception {
        mockMvc.perform(get("/api/cards/stats/balance/monthly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindYearlyBalance() throws Exception {
        mockMvc.perform(get("/api/cards/stats/balance/yearly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindMonthlyTopupAmount() throws Exception {
        mockMvc.perform(get("/api/cards/stats/topup/monthly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindYearlyTopupAmount() throws Exception {
        mockMvc.perform(get("/api/cards/stats/topup/yearly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindMonthlyTransactionAmount() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transaction/monthly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindYearlyTransactionAmount() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transaction/yearly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindMonthlyTransferAmountSender() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/monthly/sender?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindMonthlyTransferAmountReceiver() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/monthly/receiver?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindYearlyTransferAmountSender() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/yearly/sender?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindYearlyTransferAmountReceiver() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/yearly/receiver?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindMonthlyWithdrawAmount() throws Exception {
        mockMvc.perform(get("/api/cards/stats/withdraw/monthly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindYearlyWithdrawAmount() throws Exception {
        mockMvc.perform(get("/api/cards/stats/withdraw/yearly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMonthlyBalanceByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/balance/monthly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetYearlyBalanceByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/balance/yearly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMonthlyTopupAmountByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/topup/monthly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetYearlyTopupAmountByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/topup/yearly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMonthlyTransactionAmountByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transaction/monthly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetYearlyTransactionAmountByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transaction/yearly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMonthlyTransferAmountByCardSender() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/monthly/by-card/sender?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMonthlyTransferAmountByCardReceiver() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/monthly/by-card/receiver?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetYearlyTransferAmountByCardSender() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/yearly/by-card/sender?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetYearlyTransferAmountByCardReceiver() throws Exception {
        mockMvc.perform(get("/api/cards/stats/transfer/yearly/by-card/receiver?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMonthlyWithdrawAmountByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/withdraw/monthly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetYearlyWithdrawAmountByCard() throws Exception {
        mockMvc.perform(get("/api/cards/stats/withdraw/yearly/by-card?year=2024&cardNumber=" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetCardDashboard() throws Exception {
        mockMvc.perform(get("/api/cards/dashboard").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetCardDashboardByCard() throws Exception {
        mockMvc.perform(get("/api/cards/dashboard/" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}