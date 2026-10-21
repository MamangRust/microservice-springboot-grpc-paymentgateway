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
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

@AutoConfigureMockMvc
public class WithdrawControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WithdrawCommandRepository withdrawCommandRepository;

    private Long withdrawId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        Withdraw wd = new Withdraw();
        wd.setWithdrawNo(UUID.randomUUID());
        wd.setCardNumber(adminCard.getCardNumber());
        wd.setWithdrawAmount(50000L);
        wd.setWithdrawTime(new java.sql.Timestamp(System.currentTimeMillis()));
        wd.setStatus(Status.SUCCESS);
        Withdraw saved = withdrawCommandRepository.save(wd);
        withdrawId = saved.getWithdrawId();
        entityManager.flush();
    }

    @Test
    void shouldFindAllWithdraws() throws Exception {
        mockMvc.perform(get("/api/withdraws").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetWithdrawById() throws Exception {
        mockMvc.perform(get("/api/withdraws/" + withdrawId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateWithdraw() throws Exception {
        mockMvc.perform(post("/api/withdraws/create")
                .contentType("application/json")
                .content("{\"cardNumber\":\"" + adminCard.getCardNumber() + "\",\"withdrawAmount\":50000}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetStats() throws Exception {
        mockMvc.perform(get("/api/withdraws/stats/amount/monthly").param("year", "2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashWithdraw() throws Exception {
        mockMvc.perform(post("/api/withdraws/trash/" + withdrawId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteWithdraw() throws Exception {
        mockMvc.perform(delete("/api/withdraws/" + withdrawId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}