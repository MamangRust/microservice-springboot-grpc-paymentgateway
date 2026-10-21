package com.sanedge.payment_gateway.service.merchant;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantTransactionResponse;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class MerchantTransactionServiceTest extends BaseIntegrationTest {

    @Autowired
    private MerchantTransactionService merchantTransactionService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private Merchant testMerchant;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser and regularCard are pre-seeded by BaseIntegrationTest

        Merchant merchant = new Merchant();
        merchant.setName("Test Merchant " + UUID.randomUUID().toString().substring(0, 4));
        merchant.setApiKey("api-" + UUID.randomUUID().toString().substring(0, 8));
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setUserId(adminUser.getUserId());
        merchant = merchantCommandRepository.save(merchant);
        this.testMerchant = merchant;

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber(regularCard.getCardNumber());
        tx.setMerchantId(merchant.getMerchantId());
        tx.setAmount(100000L);
        tx.setPaymentMethod("CARD");
        tx.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        tx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(tx);

        entityManager.flush();
    }

    @Test
    void shouldFindAllTransactions() {
        ApiResponsePagination<List<MerchantTransactionResponse>> response = merchantTransactionService.findAll(new com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants());
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void shouldFindById() {
        var req = new com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsById();
        req.setMerchantId(testMerchant.getMerchantId());

        ApiResponsePagination<List<MerchantTransactionResponse>> response = merchantTransactionService.findById(req);
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void shouldFindByApiKey() {
        var req = new com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsByApiKey();
        req.setApiKey(testMerchant.getApiKey());

        ApiResponsePagination<List<MerchantTransactionResponse>> response = merchantTransactionService.findByApiKey(req);
        assertThat(response.getData()).isNotEmpty();
    }
}