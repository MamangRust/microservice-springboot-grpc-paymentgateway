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
import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthStatusTransaction;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusSuccess;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionStatusServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionStatusService statusService;

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
        t1.setTransactionTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setTransactionNo(UUID.randomUUID());
        t2.setCardNumber("1234567890123456");
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
        MonthStatusTransaction req = new MonthStatusTransaction();
        req.setYear(year);
        req.setMonth(4);

        ApiResponse<List<TransactionResponseMonthStatusSuccess>> response = statusService.findMonthStatusSuccess(req);
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
        ApiResponse<List<TransactionResponseYearStatusSuccess>> response = statusService.findYearlyStatusSuccess(year);
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
        MonthStatusTransaction req = new MonthStatusTransaction();
        req.setYear(year);
        req.setMonth(5);

        ApiResponse<List<TransactionResponseMonthStatusFailed>> response = statusService.findMonthStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionResponseMonthStatusFailed may = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<TransactionResponseYearStatusFailed>> response = statusService.findYearlyStatusFailed(year);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        TransactionResponseYearStatusFailed current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(1L);
    }
}