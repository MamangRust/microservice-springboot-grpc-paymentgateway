package com.sanedge.payment_gateway.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

@AutoConfigureMockMvc
public class TransactionControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    private Long transactionId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        // No manual cleanup needed
        cardNumber = adminCard.getCardNumber();

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber(cardNumber);
        tx.setAmount(100000L);
        tx.setPaymentMethod("DEBIT_CARD");
        tx.setTransactionTime(new java.sql.Timestamp(System.currentTimeMillis()));
        tx.setMerchantId(adminMerchant.getMerchantId());
        tx.setStatus(Status.SUCCESS);
        Transaction saved = transactionCommandRepository.save(tx);
        transactionId = saved.getTransactionId();
    }

    @Test
    void shouldFindAllTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetTransactionById() throws Exception {
        mockMvc.perform(get("/api/transactions/" + transactionId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions/create")
                .header("X-api-Key", adminMerchant.getApiKey())
                .contentType("application/json")
                .content("{\"cardNumber\":\"" + cardNumber + "\",\"amount\":100000,\"paymentMethod\":\"CREDIT_CARD\",\"merchantId\":" + adminMerchant.getMerchantId() + "}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions/update")
                .header("X-api-Key", adminMerchant.getApiKey())
                .contentType("application/json")
                .content("{\"transactionId\":" + transactionId + ",\"cardNumber\":\"" + cardNumber + "\",\"amount\":120000,\"paymentMethod\":\"VIRTUAL_ACCOUNT\",\"merchantId\":" + adminMerchant.getMerchantId() + "}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetStats() throws Exception {
        mockMvc.perform(get("/api/transactions/stats/amount/monthly").param("year", "2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        
        mockMvc.perform(get("/api/transactions/stats/method/monthly").param("year", "2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions/trash/" + transactionId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/" + transactionId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}