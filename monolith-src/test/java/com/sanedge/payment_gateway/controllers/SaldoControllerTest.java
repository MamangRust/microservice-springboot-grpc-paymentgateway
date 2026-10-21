package com.sanedge.payment_gateway.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.saldo.CreateSaldoRequest;
import com.sanedge.payment_gateway.domain.requests.saldo.UpdateSaldoRequest;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

@AutoConfigureMockMvc
public class SaldoControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long saldoId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminCard is seeded by BaseIntegrationTest
        saldoId = adminSaldo.getSaldoId();
        entityManager.flush();
    }

    @Test
    void shouldFindAllSaldos() throws Exception {
        mockMvc.perform(get("/api/saldos").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetSaldoById() throws Exception {
        mockMvc.perform(get("/api/saldos/" + saldoId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetSaldoByCardNumber() throws Exception {
        mockMvc.perform(get("/api/saldos/by-card/" + adminCard.getCardNumber()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateSaldo() throws Exception {
        com.sanedge.payment_gateway.models.card.Card newCard = new com.sanedge.payment_gateway.models.card.Card();
        newCard.setCardNumber("6543210987654322");
        newCard.setCardType("DEBIT");
        newCard.setCardProvider("VISA");
        newCard.setCvv("123");
        newCard.setExpireDate(java.sql.Date.valueOf(java.time.LocalDate.now().plusYears(1)));
        newCard.setUserId(adminUser.getUserId());
        
        com.sanedge.payment_gateway.models.card.Card savedCard = entityManager.merge(newCard);
        entityManager.flush();

        CreateSaldoRequest request = new CreateSaldoRequest();
        request.setCardNumber(savedCard.getCardNumber());
        request.setTotalBalance(5000L);

        mockMvc.perform(post("/api/saldos/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateSaldo() throws Exception {
        UpdateSaldoRequest request = new UpdateSaldoRequest();
        request.setSaldoId(saldoId);
        request.setCardNumber(adminCard.getCardNumber());
        request.setTotalBalance(2000L);

        mockMvc.perform(post("/api/saldos/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetTotalAmountByYear() throws Exception {
        mockMvc.perform(get("/api/saldos/total-amount/yearly?year=2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetTotalAmountByMonth() throws Exception {
        mockMvc.perform(get("/api/saldos/total-amount/monthly?year=2024&month=1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteSaldo() throws Exception {
        mockMvc.perform(delete("/api/saldos/" + saldoId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}