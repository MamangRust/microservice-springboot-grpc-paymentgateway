package com.sanedge.payment_gateway.repository.transaction.statsbycard;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthAmount;
import com.sanedge.payment_gateway.models.transaction.TransactionYearAmount;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionAmountByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountByCardRepository amountByCardRepository;

    @Autowired
    private TransactionCommandRepository commandRepository;

    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        Transaction t1 = new Transaction();
        t1.setTransactionNo(UUID.randomUUID());
        t1.setCardNumber(cardNumber);
        t1.setAmount(500000L);
        t1.setPaymentMethod("CREDIT_CARD");
        t1.setMerchantId(1L);
        t1.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(LocalDateTime.now().getYear(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        // Different card
        Transaction t2 = new Transaction();
        t2.setTransactionNo(UUID.randomUUID());
        t2.setCardNumber("6543210987654321");
        t2.setAmount(300000L);
        t2.setPaymentMethod("DEBIT_CARD");
        t2.setMerchantId(1L);
        t2.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 20, 14, 0))); // April
        t2.setStatus(Status.SUCCESS);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthlyAmountsByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransactionMonthAmount> results = amountByCardRepository.findMonthlyAmountsByCard(cardNumber, year);

        assertThat(results).hasSize(12);

        TransactionMonthAmount april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyAmountsByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransactionYearAmount> results = amountByCardRepository.findYearlyAmountsByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        TransactionYearAmount current = results.stream().filter(r -> r.getReportYear().equalsIgnoreCase(String.valueOf(year))).findFirst().orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }
}