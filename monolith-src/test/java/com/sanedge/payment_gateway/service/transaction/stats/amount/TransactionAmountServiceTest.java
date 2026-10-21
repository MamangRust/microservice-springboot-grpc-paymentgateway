package com.sanedge.payment_gateway.service.transaction.stats.amount;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionAmountServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountService amountService;

    @Autowired
    private TransactionCommandRepository commandRepository;

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        Transaction t1 = new Transaction();
        t1.setTransactionNo(UUID.randomUUID());
        t1.setCardNumber("1234567890123456");
        t1.setAmount(500000L);
        t1.setPaymentMethod("CREDIT_CARD");
        t1.setMerchantId(1L);
        t1.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0))); // April
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setTransactionNo(UUID.randomUUID());
        t2.setCardNumber("1234567890123456");
        t2.setAmount(300000L);
        t2.setPaymentMethod("DEBIT_CARD");
        t2.setMerchantId(1L);
        t2.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0))); // May
        t2.setStatus(Status.SUCCESS);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthlyAmounts() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<TransactionMonthAmountResponse>> response = amountService.findMonthlyAmounts(year);

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(12);

        TransactionMonthAmountResponse april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyAmounts() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<TransactionYearlyAmountResponse>> response = amountService.findYearlyAmounts(year);

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionYearlyAmountResponse current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(800000L);
    }
}