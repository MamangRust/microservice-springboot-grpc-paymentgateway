package com.sanedge.payment_gateway.repository.merchants;

import java.util.UUID;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.merchants.Merchant;

public class MerchantCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        entityManager.flush();
    }

    @Test
    void shouldCreateMerchant() {
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setUserId(adminUser.getUserId());

        Merchant savedMerchant = merchantCommandRepository.save(merchant);
        assertThat(savedMerchant.getMerchantId()).isNotNull();
    }

    @Test
    void shouldTrashedMerchant() {
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setUserId(adminUser.getUserId());
        merchant = merchantCommandRepository.save(merchant);

        Merchant trashed = merchantCommandRepository.trashed(merchant.getMerchantId());
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldRestoreMerchant() {
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setUserId(adminUser.getUserId());
        merchant = merchantCommandRepository.save(merchant);

        merchantCommandRepository.trashed(merchant.getMerchantId());
        Merchant restored = merchantCommandRepository.restore(merchant.getMerchantId());
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeletePermanent() {
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setUserId(adminUser.getUserId());
        merchant = merchantCommandRepository.save(merchant);

        boolean deleted = merchantCommandRepository.deletePermanent(merchant.getMerchantId());
        assertThat(deleted).isTrue();
        assertThat(merchantCommandRepository.findById(merchant.getMerchantId())).isEmpty();
    }

    @Test
    void shouldUpdateStatus() {
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setUserId(adminUser.getUserId());
        merchant = merchantCommandRepository.save(merchant);

        boolean updated = merchantCommandRepository.updateStatus(merchant.getMerchantId(), "SUCCESS");
        assertThat(updated).isTrue();
    }
}