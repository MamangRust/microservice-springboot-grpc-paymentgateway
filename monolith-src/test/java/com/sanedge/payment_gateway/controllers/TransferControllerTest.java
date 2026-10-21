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
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

@AutoConfigureMockMvc
public class TransferControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransferCommandRepository transferCommandRepository;

    private Long transferId;
    private String fromCard = "1234567890123456";
    private String toCard = "6543210987654321";

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        Transfer tr = new Transfer();
        tr.setTransferNo(UUID.randomUUID());
        tr.setTransferFrom(adminCard.getCardNumber());
        tr.setTransferTo(regularCard.getCardNumber());
        tr.setTransferAmount(50000L);
        tr.setTransferTime(new java.sql.Timestamp(System.currentTimeMillis()));
        tr.setStatus(Status.SUCCESS);
        Transfer saved = transferCommandRepository.save(tr);
        transferId = saved.getTransferId();
        entityManager.flush();
    }

    @Test
    void shouldFindAllTransfers() throws Exception {
        mockMvc.perform(get("/api/transfers").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetTransferById() throws Exception {
        mockMvc.perform(get("/api/transfers/" + transferId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateTransfer() throws Exception {
        mockMvc.perform(post("/api/transfers/create")
                .contentType("application/json")
                .content("{\"transferFrom\":\"" + adminCard.getCardNumber() + "\",\"transferTo\":\"" + regularCard.getCardNumber() + "\",\"transferAmount\":50000}")
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetStats() throws Exception {
        mockMvc.perform(get("/api/transfers/stats/amount/monthly").param("year", "2024").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashTransfer() throws Exception {
        mockMvc.perform(post("/api/transfers/trash/" + transferId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteTransfer() throws Exception {
        mockMvc.perform(delete("/api/transfers/" + transferId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}