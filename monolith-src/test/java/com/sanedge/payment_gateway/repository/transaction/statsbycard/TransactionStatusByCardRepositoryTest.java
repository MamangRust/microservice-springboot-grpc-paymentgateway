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
import com.sanedge.payment_gateway.models.transaction.TransactionMonthStatusFailed;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthStatusSuccess;
import com.sanedge.payment_gateway.models.transaction.TransactionYearStatusFailed;
import com.sanedge.payment_gateway.models.transaction.TransactionYearStatusSuccess;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionStatusByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionStatusByCardRepository statusByCardRepository;

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
        t1.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        // Different card
        Transaction t2 = new Transaction();
        t2.setTransactionNo(UUID.randomUUID());
        t2.setCardNumber("6543210987654321");
        t2.setAmount(300000L);
        t2.setPaymentMethod("DEBIT_CARD");
        t2.setMerchantId(1L);
        t2.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        t2.setStatus(Status.FAILED);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthTransactionStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransactionMonthStatusSuccess> results = statusByCardRepository.findMonthTransactionStatusSuccessByCard(cardNumber, year, 1, year, 12);

        assertThat(results).isNotEmpty();

        TransactionMonthStatusSuccess april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindMonthTransactionStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransactionMonthStatusFailed> results = statusByCardRepository.findMonthTransactionStatusFailedByCard(cardNumber, year, 1, year, 12);

        assertThat(results).isNotEmpty();

        TransactionMonthStatusFailed may = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(0L); // Should be 0 for this card;
    }

    @Test
    void testFindYearlyTransactionStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransactionYearStatusSuccess> results = statusByCardRepository.findYearlyTransactionStatusSuccessByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        TransactionYearStatusSuccess current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyTransactionStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransactionYearStatusFailed> results = statusByCardRepository.findYearlyTransactionStatusFailedByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        TransactionYearStatusFailed current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(0L);
    }
}