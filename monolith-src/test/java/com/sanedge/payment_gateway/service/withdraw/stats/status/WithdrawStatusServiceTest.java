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
import com.sanedge.payment_gateway.domain.requests.withdraws.MonthStatusWithdraw;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawStatusServiceTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawStatusService statusService;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminCard is pre-seeded by BaseIntegrationTest
        Long currentYear = (long) LocalDateTime.now().getYear();

        // Success w1
        Withdraw w1 = new Withdraw();
        w1.setWithdrawNo(UUID.randomUUID());
        w1.setCardNumber(adminCard.getCardNumber());
        w1.setWithdrawAmount(500000L);
        w1.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        w1.setStatus(Status.SUCCESS);
        commandRepository.save(w1);

        // Failed w2
        Withdraw w2 = new Withdraw();
        w2.setWithdrawNo(UUID.randomUUID());
        w2.setCardNumber(adminCard.getCardNumber());
        w2.setWithdrawAmount(300000L);
        w2.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        w2.setStatus(Status.FAILED);
        commandRepository.save(w2);

        entityManager.flush();
    }

    @Test
    void testFindMonthStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusWithdraw req = new MonthStatusWithdraw();
        req.setYear(year);
        req.setMonth(4);

        ApiResponse<List<WithdrawResponseMonthStatusSuccess>> response = statusService.findMonthStatusSuccess(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        WithdrawResponseMonthStatusSuccess april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getReportYear()).isEqualTo(String.valueOf(year));
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<WithdrawResponseYearStatusSuccess>> response = statusService.findYearlyStatusSuccess(year);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        WithdrawResponseYearStatusSuccess current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindMonthStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        MonthStatusWithdraw req = new MonthStatusWithdraw();
        req.setYear(year);
        req.setMonth(5);

        ApiResponse<List<WithdrawResponseMonthStatusFailed>> response = statusService.findMonthStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        WithdrawResponseMonthStatusFailed may = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getReportYear()).isEqualTo(String.valueOf(year));
        assertThat(may.getTotalFailed()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        ApiResponse<List<WithdrawResponseYearStatusFailed>> response = statusService.findYearlyStatusFailed(year);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();

        WithdrawResponseYearStatusFailed current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(1L);
    }
}