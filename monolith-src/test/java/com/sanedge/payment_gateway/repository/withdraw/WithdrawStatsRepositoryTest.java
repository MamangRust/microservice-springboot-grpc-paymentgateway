package com.sanedge.payment_gateway.repository.withdraw;

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
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthlyAmount;
import com.sanedge.payment_gateway.repository.withdraw.stats.WithdrawAmountRepository;

public class WithdrawStatsRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawAmountRepository withdrawAmountRepository;

    @Autowired
    private WithdrawCommandRepository withdrawCommandRepository;

    @BeforeEach
    void setUp() {
        withdrawCommandRepository.deleteAll();

        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber("1111222233334444");
        withdraw.setWithdrawAmount(20000L);
        withdraw.setWithdrawTime(new Timestamp(System.currentTimeMillis()));
        withdraw.setStatus(Status.SUCCESS);
        withdrawCommandRepository.save(withdraw);
    }

    @Test
    void shouldGetMonthlyWithdrawAmounts() {
        Long currentYear = (long) LocalDate.now().getYear();
        List<WithdrawMonthlyAmount> stats = withdrawAmountRepository.findMonthlyWithdraws(currentYear);
        assertThat(stats).isNotEmpty();
        assertThat(stats.stream().anyMatch(s -> s.getTotalAmount() > 0)).isTrue();
    }
}