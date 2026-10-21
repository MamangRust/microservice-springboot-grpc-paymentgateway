package com.sanedge.payment_gateway.repository.merchants.statsbyid;

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
import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyAmount;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class MerchantAmountByIdRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private MerchantAmountByIdRepository merchantAmountByIdRepository;

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
    void shouldFindMonthlyAmountById() {
        Merchant merchant = merchantCommandRepository.findAll().get(0);
        List<MerchantMonthlyAmount> stats = merchantAmountByIdRepository.findMonthlyAmountById(merchant.getMerchantId(), (long) LocalDate.now().getYear());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldFindYearlyAmountById() {
        Merchant merchant = merchantCommandRepository.findAll().get(0);
        var stats = merchantAmountByIdRepository.findYearlyAmountById(merchant.getMerchantId(), (long) LocalDate.now().getYear());
        assertThat(stats).isNotEmpty();
    }
}