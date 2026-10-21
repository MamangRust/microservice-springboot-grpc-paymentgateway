package com.sanedge.payment_gateway.service.transaction;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transaction.CreateTransactionRequest;
import com.sanedge.payment_gateway.domain.requests.transaction.UpdateTransactionRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionCommandService commandService;

    @Autowired
    private TransactionCommandRepository commandRepository;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    private Long transactionId;
    private String apiKey = "test-api-key";

    @Autowired
    private com.sanedge.payment_gateway.repository.card.CardCommandRepository cardCommandRepository;

    @Autowired
    private com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository saldoCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();
        merchantCommandRepository.deleteAll();
        saldoCommandRepository.deleteAll();
        cardCommandRepository.deleteAll();
        entityManager.flush();

        // Merchant uses pre-seeded admin user (ID 1)
        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey(apiKey);
        merchant.setMerchantId(null);
        merchant.setUserId(1L);
        merchant.setStatus(Status.SUCCESS);
        Merchant savedMerchant = merchantCommandRepository.save(merchant);
        this.merchantId = savedMerchant.getMerchantId();

        // Seed Card
        com.sanedge.payment_gateway.models.card.Card card = new com.sanedge.payment_gateway.models.card.Card();
        card.setCardNumber("1234567890123456");
        card.setCardType("CREDIT_CARD");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        card.setUserId(1L);
        cardCommandRepository.save(card);

        // Seed Saldo
        com.sanedge.payment_gateway.models.saldo.Saldo saldo = new com.sanedge.payment_gateway.models.saldo.Saldo();
        saldo.setCardNumber("1234567890123456");
        saldo.setTotalBalance(1000000L);
        saldoCommandRepository.save(saldo);

        Transaction transaction = new Transaction();
        transaction.setTransactionNo(UUID.randomUUID());
        transaction.setCardNumber("1234567890123456");
        transaction.setAmount(100000L);
        transaction.setPaymentMethod("CREDIT_CARD");
        transaction.setMerchantId(this.merchantId);
        transaction.setTransactionTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));

        Transaction saved = commandRepository.save(transaction);
        transactionId = saved.getTransactionId();
    }

    private Long merchantId;

    @Test
    void testCreate() {
        CreateTransactionRequest req = new CreateTransactionRequest();
        req.setCardNumber("1234567890123456");
        req.setAmount(150000L);
        req.setPaymentMethod("CREDIT_CARD");
        req.setMerchantId(merchantId);
        req.setTransactionTime(LocalDateTime.now());

        ApiResponse<TransactionResponse> response = commandService.create(apiKey, req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getAmount()).isEqualTo(150000L);
    }

    @Test
    void testUpdate() {
        UpdateTransactionRequest req = new UpdateTransactionRequest();
        req.setTransactionId(transactionId);
        req.setCardNumber("1234567890123456");
        req.setAmount(200000L);
        req.setPaymentMethod("CREDIT_CARD");
        req.setMerchantId(merchantId);
        req.setTransactionTime(LocalDateTime.now());

        ApiResponse<TransactionResponse> response = commandService.update(apiKey, req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getAmount()).isEqualTo(200000L);
    }

    @Test
    void testTrashed() {
        ApiResponse<TransactionResponseDeleteAt> response = commandService.trashed(transactionId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getDeletedAt()).isNotNull();
    }

    @Test
    void testRestore() {
        commandService.trashed(transactionId);
        ApiResponse<TransactionResponseDeleteAt> response = commandService.restore(transactionId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getDeletedAt()).isNull();
    }

    @Test
    void testDeletePermanent() {
        commandService.trashed(transactionId);
        ApiResponse<Boolean> response = commandService.deletePermanent(transactionId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void testRestoreAll() {
        commandService.trashed(transactionId);
        ApiResponse<Boolean> response = commandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void testDeleteAll() {
        commandService.trashed(transactionId);
        ApiResponse<Boolean> response = commandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }
}