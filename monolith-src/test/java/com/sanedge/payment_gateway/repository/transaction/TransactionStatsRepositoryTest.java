package com.sanedge.payment_gateway.repository.transaction;

        import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthAmount;
import com.sanedge.payment_gateway.repository.transaction.stats.TransactionAmountRepository;

public class TransactionStatsRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountRepository transactionAmountRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @BeforeEach
    void setUp() {
        transactionCommandRepository.deleteAll();

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber("1111222233334444");
        tx.setMerchantId(1L);
        tx.setAmount(100000L);
        tx.setPaymentMethod("CARD");
        tx.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        tx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(tx);
    }

    @Test
    void shouldGetMonthlyTransactionAmounts() {
        int currentYear = LocalDate.now().getYear();
        List<TransactionMonthAmount> stats = transactionAmountRepository.findMonthlyAmounts((long) currentYear);
        assertThat(stats).isNotEmpty();
        assertThat(stats.stream().anyMatch(s -> s.getTotalAmount() > 0)).isTrue();
    }
}