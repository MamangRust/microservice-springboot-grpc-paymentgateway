package com.sanedge.payment_gateway.repository.topup.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.models.topup.TopupMonthStatusFailed;
import com.sanedge.payment_gateway.models.topup.TopupMonthStatusSuccess;
import com.sanedge.payment_gateway.models.topup.TopupYearStatusFailed;
import com.sanedge.payment_gateway.models.topup.TopupYearStatusSuccess;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class TopupStatusRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TopupStatusRepository topupStatusRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber("12345678909999");
        topup.setTopupAmount(100000L);
        topup.setTopupMethod("VA_BCA");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);

        Topup failedTopup = new Topup();
        failedTopup.setTopupNo(UUID.randomUUID());
        failedTopup.setCardNumber("12345678919999");
        failedTopup.setTopupAmount(50000L);
        failedTopup.setTopupMethod("VA_BCA");
        failedTopup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        failedTopup.setStatus(Status.FAILED);
        topupCommandRepository.save(failedTopup);
    }

    @Test
    void shouldFindMonthTopupStatusSuccess() {
        List<TopupMonthStatusSuccess> results = topupStatusRepository.findMonthTopupStatusSuccess((long) 2026, 1, (long) 2026, 12);
        assertThat(results).isNotEmpty();
    }

    @Test
    void shouldFindYearlyTopupStatusSuccess() {
        List<TopupYearStatusSuccess> results = topupStatusRepository.findYearlyTopupStatusSuccess(2026L);
        assertThat(results).isNotEmpty();
        TopupYearStatusSuccess current = results.get(0);
        assertThat(current.getTotalSuccess()).isNotNull();
    }

    @Test
    void shouldFindYearlyTopupStatusFailed() {
        List<TopupYearStatusFailed> results = topupStatusRepository.findYearlyTopupStatusFailed(2026L);
        assertThat(results).isNotEmpty();
        TopupYearStatusFailed current = results.get(0);
        assertThat(current.getTotalFailed()).isNotNull();
    }
}