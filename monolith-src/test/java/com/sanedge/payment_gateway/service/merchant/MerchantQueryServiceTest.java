package com.sanedge.payment_gateway.service.merchant;

import java.util.UUID;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponse;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;

public class MerchantQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private MerchantQueryService merchantQueryService;

    @Autowired
    private MerchantCommandService merchantCommandService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser is pre-seeded by BaseIntegrationTest

        CreateMerchantRequest req = new CreateMerchantRequest();
        req.setName("Test Merchant " + UUID.randomUUID().toString().substring(0, 4));
        req.setUserId(adminUser.getUserId());
        ApiResponse<MerchantResponse> response = merchantCommandService.createMerchant(req);
        this.apiKeyGenerated = response.getData().getApiKey();
    }

    private String apiKeyGenerated;

    @Autowired
    private com.sanedge.payment_gateway.repository.user.UserCommandRepository userCommandRepository;

    @Test
    void shouldFindById() {
        MerchantResponse merchant = merchantQueryService.findAll(new com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants()).getData().get(0);
        ApiResponse<MerchantResponse> response = merchantQueryService.findById(merchant.getId());
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void shouldFindByApiKey() {
        ApiResponse<MerchantResponse> response = merchantQueryService.findByApiKey(apiKeyGenerated);
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void shouldFindAll() {
        var req = new com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants();
        var response = merchantQueryService.findAll(req);
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void shouldFindByActive() {
        var req = new com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants();
        var response = merchantQueryService.findByActive(req);
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void shouldFindByTrashed() {
        MerchantResponse merchant = merchantQueryService.findAll(new com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants()).getData().get(0);
        merchantCommandService.trashMerchant(merchant.getId());

        var req = new com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants();
        var response = merchantQueryService.findByTrashed(req);
        assertThat(response.getData()).isNotNull();
    }

    @Test
    void shouldFindByUserId() {
        ApiResponse<List<MerchantResponse>> response = merchantQueryService.findByUserId(adminUser.getUserId());
        assertThat(response.getData()).isNotEmpty();
    }
}