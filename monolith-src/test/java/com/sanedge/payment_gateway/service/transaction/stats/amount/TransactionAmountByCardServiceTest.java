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
import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthYearPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionAmountByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionAmountByCardService amountByCardService;

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

        Transaction t2 = new Transaction();
        t2.setTransactionNo(UUID.randomUUID());
        t2.setCardNumber("6543210987654321");
        t2.setAmount(300000L);
        t2.setPaymentMethod("DEBIT_CARD");
        t2.setMerchantId(1L);
        t2.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 20, 14, 0)));
        t2.setStatus(Status.SUCCESS);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthlyAmountsByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearPaymentMethod req = new MonthYearPaymentMethod();
        req.setCardNumber(cardNumber);
        req.setYear(year);

        ApiResponse<List<TransactionMonthAmountResponse>> response = amountByCardService.findMonthlyAmounts(req);

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(12);

        TransactionMonthAmountResponse april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyAmountsByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearPaymentMethod req = new MonthYearPaymentMethod();
        req.setCardNumber(cardNumber);
        req.setYear(year);

        ApiResponse<List<TransactionYearlyAmountResponse>> response = amountByCardService.findYearlyAmounts(req);

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionYearlyAmountResponse current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }
}