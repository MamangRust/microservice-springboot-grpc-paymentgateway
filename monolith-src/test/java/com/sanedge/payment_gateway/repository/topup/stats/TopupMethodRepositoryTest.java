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
import com.sanedge.payment_gateway.models.topup.TopupMonthMethod;
import com.sanedge.payment_gateway.models.topup.TopupYearMethod;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class TopupMethodRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TopupMethodRepository topupMethodRepository;

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
    }

    @Test
    void shouldFindMonthlyTopupMethods() {
        List<TopupMonthMethod> results = topupMethodRepository.findMonthlyTopupMethods(2026L);
        assertThat(results).isNotEmpty();
    }

    @Test
    void shouldFindYearlyTopupMethods() {
        List<TopupYearMethod> results = topupMethodRepository.findYearlyTopupMethods(2026L);
        assertThat(results).isNotEmpty();
        TopupYearMethod current = results.get(results.size() - 1);
        assertThat(current.getTotalTopups()).isNotNull();
    }
}