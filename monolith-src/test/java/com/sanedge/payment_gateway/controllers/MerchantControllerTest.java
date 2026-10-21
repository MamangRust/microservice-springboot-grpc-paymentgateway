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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.payment_gateway.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;

@AutoConfigureMockMvc
public class MerchantControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long merchantId;
    private String apiKey = "ctrl-api-key";

    @BeforeEach
    void setUp() {
        merchantId = adminMerchant.getMerchantId();
        apiKey = adminMerchant.getApiKey();
    }

    @Test
    void shouldFindAllMerchants() throws Exception {
        mockMvc.perform(get("/api/merchants").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindActiveMerchants() throws Exception {
        mockMvc.perform(get("/api/merchants/active").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldFindTrashedMerchants() throws Exception {
        merchantCommandRepository.trashed(merchantId);
        mockMvc.perform(get("/api/merchants/trashed").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMerchantById() throws Exception {
        mockMvc.perform(get("/api/merchants/" + merchantId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMerchantByApiKey() throws Exception {
        mockMvc.perform(get("/api/merchants/by-api-key")
                .param("apiKey", apiKey)
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldGetMerchantByUserId() throws Exception {
        mockMvc.perform(get("/api/merchants/by-user/" + adminUser.getUserId()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldCreateMerchant() throws Exception {
        CreateMerchantRequest request = new CreateMerchantRequest();
        request.setName("New Merchant " + UUID.randomUUID());
        request.setUserId(adminUser.getUserId());

        mockMvc.perform(post("/api/merchants/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldUpdateMerchant() throws Exception {
        UpdateMerchantRequest request = new UpdateMerchantRequest();
        request.setMerchantId(merchantId);
        request.setUserId(adminUser.getUserId());
        request.setName("Updated Merchant");
        request.setStatus("SUCCESS");

        mockMvc.perform(post("/api/merchants/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldTrashMerchant() throws Exception {
        mockMvc.perform(post("/api/merchants/trash/" + merchantId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldRestoreMerchant() throws Exception {
        merchantCommandRepository.trashed(merchantId);
        mockMvc.perform(post("/api/merchants/restore/" + merchantId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void shouldDeleteMerchant() throws Exception {
        mockMvc.perform(delete("/api/merchants/" + merchantId).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }
}