package com.sanedge.payment_gateway.repository.topup.statsbycard;

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

public class TopupStatusByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TopupStatusByCardRepository topupStatusByCardRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        topupCommandRepository.deleteAll();

        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber("1234567890");
        topup.setTopupAmount(100000L);
        topup.setTopupMethod("VA_BCA");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);

        Topup failedTopup = new Topup();
        failedTopup.setTopupNo(UUID.randomUUID());
        failedTopup.setCardNumber("1234567890");
        failedTopup.setTopupAmount(50000L);
        failedTopup.setTopupMethod("VA_BCA");
        failedTopup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        failedTopup.setStatus(Status.FAILED);
        topupCommandRepository.save(failedTopup);
    }

    @Test
    void shouldFindMonthTopupStatusSuccessByCard() {
        List<TopupMonthStatusSuccess> results = topupStatusByCardRepository.findMonthTopupStatusSuccessByCard("1234567890", (long) 2026, 1, (long) 2026, 12);
        assertThat(results).hasSize(12);
    }

    @Test
    void shouldFindMonthTopupStatusFailedByCard() {
        List<TopupMonthStatusFailed> results = topupStatusByCardRepository.findMonthTopupStatusFailedByCard("1234567890", (long) 2026, 1, (long) 2026, 12);
        assertThat(results).hasSize(12);
    }

    @Test
    void shouldFindYearlyTopupStatusSuccessByCard() {
        List<TopupYearStatusSuccess> results = topupStatusByCardRepository.findYearlyTopupStatusSuccessByCard("1234567890", 2026L);
        assertThat(results).isNotEmpty();
        TopupYearStatusSuccess current = results.get(0);
        assertThat(current.getTotalSuccess()).isNotNull();
    }

    @Test
    void shouldFindYearlyTopupStatusFailedByCard() {
        List<TopupYearStatusFailed> results = topupStatusByCardRepository.findYearlyTopupStatusFailedByCard("1234567890", 2026L);
        assertThat(results).isNotEmpty();
        TopupYearStatusFailed current = results.get(0);
        assertThat(current.getTotalFailed()).isNotNull();
    }
}