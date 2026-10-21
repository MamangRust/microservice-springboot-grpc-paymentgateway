package com.sanedge.payment_gateway.repository.merchants.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyTotalAmount;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class MerchantTotalAmountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private MerchantTotalAmountRepository merchantTotalAmountRepository;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @BeforeEach
    void setUp() {
        transactionCommandRepository.deleteAll();
        merchantCommandRepository.deleteAll();

        Merchant merchant = new Merchant();
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setMerchantId(null);
        merchant.setUserId(1L);
        merchant = merchantCommandRepository.save(merchant);

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber("1111222233334444");
        tx.setMerchantId(merchant.getMerchantId());
        tx.setAmount(100000L);
        tx.setPaymentMethod("CARD");
        tx.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        tx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(tx);
    }

    @Test
    void shouldFindMonthlyTotalAmount() {
        List<MerchantMonthlyTotalAmount> stats = merchantTotalAmountRepository.findMonthlyTotalAmount(LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldFindYearlyTotalAmount() {
        int year = LocalDate.now().getYear();
        var stats = merchantTotalAmountRepository.findYearlyTotalAmount((long) year, (long) (year - 1));
        assertThat(stats).isNotEmpty();
    }
}