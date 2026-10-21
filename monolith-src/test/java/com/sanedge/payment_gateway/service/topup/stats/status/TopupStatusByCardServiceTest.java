package com.sanedge.payment_gateway.service.topup.stats.status;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.MonthTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.YearTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusSuccess;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class TopupStatusByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private TopupStatusByCardService topupStatusByCardService;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        topupCommandRepository.deleteAll();

        Topup topup = new Topup();
        topup.setCardNumber("1234567890");
        topup.setTopupAmount(100000L);
        topup.setTopupMethod("VA_BCA");
        topup.setTopupNo(java.util.UUID.randomUUID());
        topup.setTopupTime(new java.sql.Timestamp(System.currentTimeMillis()));
        topup.setStatus(com.sanedge.payment_gateway.enums.Status.SUCCESS);
        topupCommandRepository.save(topup);
    }

    @Test
    void shouldFindMonthStatusSuccess() {
        MonthTopupStatusCardNumber req = new MonthTopupStatusCardNumber();
        req.setCardNumber("1234567890");
        req.setYear(2026L);
        req.setMonth(4);
        ApiResponse<List<TopupResponseMonthStatusSuccess>> response = topupStatusByCardService.findMonthStatusSuccess(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearlyStatusSuccess() {
        YearTopupStatusCardNumber req = new YearTopupStatusCardNumber();
        req.setCardNumber("1234567890");
        req.setYear(2026L);
        ApiResponse<List<TopupResponseYearStatusSuccess>> response = topupStatusByCardService.findYearlyStatusSuccess(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindMonthStatusFailed() {
        MonthTopupStatusCardNumber req = new MonthTopupStatusCardNumber();
        req.setCardNumber("1234567890");
        req.setYear(2026L);
        req.setMonth(4);
        ApiResponse<List<TopupResponseMonthStatusFailed>> response = topupStatusByCardService.findMonthStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearlyStatusFailed() {
        YearTopupStatusCardNumber req = new YearTopupStatusCardNumber();
        req.setCardNumber("1234567890");
        req.setYear(2026L);
        ApiResponse<List<TopupResponseYearStatusFailed>> response = topupStatusByCardService.findYearlyStatusFailed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}