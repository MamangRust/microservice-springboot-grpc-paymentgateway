package com.sanedge.payment_gateway.service.topup.stats.amount;

        import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupYearlyAmountResponse;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class TopupAmountServiceTest extends BaseIntegrationTest {

    @Autowired
    private TopupAmountService topupAmountService;

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
    void shouldFindMonthlyAmounts() {
        ApiResponse<List<TopupMonthAmountResponse>> response = topupAmountService.findMonthlyAmounts((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearlyAmounts() {
        ApiResponse<List<TopupYearlyAmountResponse>> response = topupAmountService.findYearlyAmounts(2026L);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}