package com.sanedge.payment_gateway.service.transaction.stats.method;

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
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionYearMethodResponse;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionMethodByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionMethodByCardService methodByCardService;

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
    void testFindMonthlyMethodByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearPaymentMethod req = new MonthYearPaymentMethod();
        req.setCardNumber(cardNumber);
        req.setYear(year);

        ApiResponse<List<TransactionMonthMethodResponse>> response = methodByCardService.findMonthlyMethod(req);

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionMonthMethodResponse creditApril = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr") && r.getPaymentMethod().equals("CREDIT_CARD"))
                .findFirst()
                .orElseThrow();
        assertThat(creditApril.getTotalTransactions()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyMethodByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthYearPaymentMethod req = new MonthYearPaymentMethod();
        req.setCardNumber(cardNumber);
        req.setYear(year);

        ApiResponse<List<TransactionYearMethodResponse>> response = methodByCardService.findYearlyMethod(req);

        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionYearMethodResponse creditCurrent = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)) && r.getPaymentMethod().equals("CREDIT_CARD"))
                .findFirst()
                .orElseThrow();
        assertThat(creditCurrent.getTotalTransactions()).isEqualTo(1L);
    }
}