package com.sanedge.payment_gateway.repository.merchants;

import java.util.UUID;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.merchants.Merchant;

public class MerchantQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private MerchantQueryRepository merchantQueryRepository;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @BeforeEach
    void setUp() {
        merchantCommandRepository.deleteAll();

        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setUserId(1L);
        merchantCommandRepository.save(merchant);
    }

    @Test
    void shouldFindByName() {
        Optional<Merchant> merchant = merchantQueryRepository.findByName("Test Merchant");
        assertThat(merchant).isPresent();
    }

    @Test
    void shouldFindByApiKey() {
        Optional<Merchant> merchant = merchantQueryRepository.findByApiKey("test-api-key");
        assertThat(merchant).isPresent();
    }

    @Test
    void shouldExistsByName() {
        boolean exists = merchantQueryRepository.existsByName("Test Merchant");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldFindActiveMerchants() {
        var page = merchantQueryRepository.findActiveMerchants(null, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void shouldFindTrashedMerchants() {
        Merchant merchant = merchantQueryRepository.findByName("Test Merchant").get();
        merchantCommandRepository.trashed(merchant.getMerchantId());

        var page = merchantQueryRepository.findTrashedMerchants(null, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void shouldFindMerchantById() {
        Merchant merchant = merchantQueryRepository.findByName("Test Merchant").get();
        Optional<Merchant> found = merchantQueryRepository.findMerchantById(merchant.getMerchantId());
        assertThat(found).isPresent();
    }

    @Test
    void shouldFindByUserId() {
        java.util.List<Merchant> merchants = merchantQueryRepository.findByUserId(1L);
        assertThat(merchants).isNotEmpty();
    }
}