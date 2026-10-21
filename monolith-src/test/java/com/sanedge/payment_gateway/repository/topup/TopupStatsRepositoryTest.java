package com.sanedge.payment_gateway.repository.topup;

        import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.models.topup.TopupMonthAmount;
import com.sanedge.payment_gateway.repository.topup.stats.TopupAmountRepository;

public class TopupStatsRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TopupAmountRepository topupAmountRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        topupCommandRepository.deleteAll();

        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber("1111222233334444");
        topup.setTopupAmount(50000L);
        topup.setTopupMethod("BANK_TRANSFER");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);
    }

    @Test
    void shouldGetMonthlyTopupAmounts() {
        int currentYear = LocalDate.now().getYear();
        List<TopupMonthAmount> stats = topupAmountRepository.findMonthlyTopupAmounts((long) currentYear);
        assertThat(stats).isNotEmpty();
        assertThat(stats.stream().anyMatch(s -> s.getTotalAmount() > 0)).isTrue();
    }
}