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
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

@AutoConfigureMockMvc
public class TopupControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    private Long topupId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        // No manual cleanup needed
        cardNumber = adminCard.getCardNumber();

        Topup topup = new Topup();
        topup.setCardNumber(cardNumber);
        topup.setTopupAmount(60000L);
        topup.setTopupMethod("CREDIT_CARD");
        topup.setTopupNo(UUID.randomUUID());
        topup.setTopupTime(new java.sql.Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        Topup saved = topupCommandRepository.save(topup);
        topupId = saved.getTopupId();
    }

    @Test
    void shouldFindAllTopups() throws Exception {
        mockMvc.perform(get("/api/topups").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetTopupById() throws Exception {
        mockMvc.perform(get("/api/topups/" + topupId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateTopup() throws Exception {
        mockMvc.perform(post("/api/topups/create")
                .contentType("application/json")
                .content("{\"cardNumber\":\"" + cardNumber + "\",\"topupAmount\":60000,\"topupMethod\":\"BANK_TRANSFER\"}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateTopup() throws Exception {
        mockMvc.perform(post("/api/topups/update")
                .contentType("application/json")
                .content("{\"topupId\":" + topupId + ",\"cardNumber\":\"" + cardNumber + "\",\"topupAmount\":70000,\"topupMethod\":\"E_WALLET\"}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetStats() throws Exception {
        mockMvc.perform(get("/api/topups/stats/amount/monthly").param("year", "2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        
        mockMvc.perform(get("/api/topups/stats/method/monthly").param("year", "2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashTopup() throws Exception {
        mockMvc.perform(post("/api/topups/trash/" + topupId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteTopup() throws Exception {
        mockMvc.perform(delete("/api/topups/" + topupId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}