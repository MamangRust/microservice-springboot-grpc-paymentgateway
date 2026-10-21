package com.sanedge.payment_gateway.service.topup.stats.method;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.topup.stats.YearMonthMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupYearlyMethodResponse;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class TopupMethodByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private TopupMethodByCardService topupMethodByCardService;

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
    void shouldFindMonthlyMethods() {
        YearMonthMethod req = new YearMonthMethod();
        req.setCardNumber("1234567890");
        req.setYear(2026L);
        ApiResponse<List<TopupMonthMethodResponse>> response = topupMethodByCardService.findMonthlyMethods(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearlyMethods() {
        YearMonthMethod req = new YearMonthMethod();
        req.setCardNumber("1234567890");
        req.setYear(2026L);
        ApiResponse<List<TopupYearlyMethodResponse>> response = topupMethodByCardService.findYearlyMethods(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}