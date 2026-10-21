package com.sanedge.payment_gateway.service.withdraw.stats.status;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.MonthStatusWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearStatusWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawStatusByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawStatusByCardService statusByCardService;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        // Success w1
        Withdraw w1 = new Withdraw();
        w1.setWithdrawNo(UUID.randomUUID());
        w1.setCardNumber(cardNumber);
        w1.setWithdrawAmount(500000L);
        w1.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        w1.setStatus(Status.SUCCESS);
        commandRepository.save(w1);

        // Failed w2 (different card)
        Withdraw w2 = new Withdraw();
        w2.setWithdrawNo(UUID.randomUUID());
        w2.setCardNumber("6543210987654321");
        w2.setWithdrawAmount(300000L);
        w2.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        w2.setStatus(Status.FAILED);
        commandRepository.save(w2);
    }

    @Test
    void testFindMonthStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusWithdrawCardNumber req = new MonthStatusWithdrawCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year.intValue());
        req.setMonth(4);

        ApiResponse<List<WithdrawResponseMonthStatusSuccess>> response = statusByCardService.findMonthStatusSuccessByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        WithdrawResponseMonthStatusSuccess april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        YearStatusWithdrawCardNumber req = new YearStatusWithdrawCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year.intValue());

        ApiResponse<List<WithdrawResponseYearStatusSuccess>> response = statusByCardService.findYearlyStatusSuccessByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        WithdrawResponseYearStatusSuccess current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindMonthStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusWithdrawCardNumber req = new MonthStatusWithdrawCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year.intValue());
        req.setMonth(5);

        ApiResponse<List<WithdrawResponseMonthStatusFailed>> response = statusByCardService.findMonthStatusFailedByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");

        WithdrawResponseMonthStatusFailed may = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(0L);
    }

    @Test
    void testFindYearlyStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        YearStatusWithdrawCardNumber req = new YearStatusWithdrawCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year.intValue());

        ApiResponse<List<WithdrawResponseYearStatusFailed>> response = statusByCardService.findYearlyStatusFailedByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");

        WithdrawResponseYearStatusFailed current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(0L);
    }
}