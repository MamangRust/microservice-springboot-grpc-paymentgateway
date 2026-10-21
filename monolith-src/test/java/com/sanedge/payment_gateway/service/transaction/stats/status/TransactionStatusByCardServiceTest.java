package com.sanedge.payment_gateway.service.transaction.stats.status;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transaction.statsbycard.MonthStatusTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.statsbycard.YearStatusTransactionCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusSuccess;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionStatusByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionStatusByCardService statusByCardService;

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
        t2.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        t2.setStatus(Status.FAILED);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusTransactionCardNumber req = new MonthStatusTransactionCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year);
        req.setMonth(4);

        ApiResponse<List<TransactionResponseMonthStatusSuccess>> response = statusByCardService.findMonthStatusSuccess(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionResponseMonthStatusSuccess april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        YearStatusTransactionCardNumber req = new YearStatusTransactionCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year);

        ApiResponse<List<TransactionResponseYearStatusSuccess>> response = statusByCardService.findYearlyStatusSuccess(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionResponseYearStatusSuccess current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindMonthStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusTransactionCardNumber req = new MonthStatusTransactionCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year);
        req.setMonth(5);

        ApiResponse<List<TransactionResponseMonthStatusFailed>> response = statusByCardService.findMonthStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        // Since t2 is a different card, it should be 0 for this card
        TransactionResponseMonthStatusFailed may = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(0L);
    }

    @Test
    void testFindYearlyStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        YearStatusTransactionCardNumber req = new YearStatusTransactionCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year);

        ApiResponse<List<TransactionResponseYearStatusFailed>> response = statusByCardService.findYearlyStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");

        TransactionResponseYearStatusFailed current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(0L);
    }
}