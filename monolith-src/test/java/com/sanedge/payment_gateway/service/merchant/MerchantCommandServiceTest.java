package com.sanedge.payment_gateway.service.merchant;

        import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponse;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;

public class MerchantCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private MerchantCommandService merchantCommandService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @BeforeEach
    void setUp() {
        merchantCommandRepository.deleteAll();
        userCommandRepository.deleteAll();

        com.sanedge.payment_gateway.models.User user = new com.sanedge.payment_gateway.models.User();
        user.setUsername("cmduser");
        user.setEmail("cmd@test.com");
        user.setPassword("password");
        user.setFirstname("Cmd");
        user.setLastname("User");
        user = userCommandRepository.save(user);
        this.userId = user.getUserId();
    }

    private Long userId;

    @Autowired
    private com.sanedge.payment_gateway.repository.user.UserCommandRepository userCommandRepository;

    @Test
    void shouldCreateMerchant() {
        CreateMerchantRequest req = new CreateMerchantRequest();
        req.setName("Test Merchant Cmd");
        req.setUserId(userId);

        ApiResponse<MerchantResponse> response = merchantCommandService.createMerchant(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldUpdateMerchant() {
        CreateMerchantRequest createReq = new CreateMerchantRequest();
        createReq.setName("Test Merchant Update");
        createReq.setUserId(userId);
        MerchantResponse merchant = merchantCommandService.createMerchant(createReq).getData();

        com.sanedge.payment_gateway.domain.requests.merchant.UpdateMerchantRequest updateReq = new com.sanedge.payment_gateway.domain.requests.merchant.UpdateMerchantRequest();
        updateReq.setMerchantId(merchant.getId());
        updateReq.setName("Updated Merchant");
        updateReq.setUserId(userId);
        updateReq.setStatus("SUCCESS");

        ApiResponse<MerchantResponse> response = merchantCommandService.updateMerchant(updateReq);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getName()).isEqualTo("Updated Merchant");
    }

    @Test
    void shouldTrashMerchant() {
        CreateMerchantRequest createReq = new CreateMerchantRequest();
        createReq.setName("Test Merchant Update");
        createReq.setUserId(userId);
        MerchantResponse merchant = merchantCommandService.createMerchant(createReq).getData();

        ApiResponse<com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponseDeleteAt> response = merchantCommandService.trashMerchant(merchant.getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldRestoreMerchant() {
        CreateMerchantRequest createReq = new CreateMerchantRequest();
        createReq.setName("Test Merchant Update");
        createReq.setUserId(userId);
        MerchantResponse merchant = merchantCommandService.createMerchant(createReq).getData();

        merchantCommandService.trashMerchant(merchant.getId());
        ApiResponse<com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponseDeleteAt> response = merchantCommandService.restoreMerchant(merchant.getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteMerchant() {
        CreateMerchantRequest createReq = new CreateMerchantRequest();
        createReq.setName("Test Merchant Update");
        createReq.setUserId(userId);
        MerchantResponse merchant = merchantCommandService.createMerchant(createReq).getData();

        ApiResponse<Boolean> response = merchantCommandService.deleteMerchant(merchant.getId());
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }
}