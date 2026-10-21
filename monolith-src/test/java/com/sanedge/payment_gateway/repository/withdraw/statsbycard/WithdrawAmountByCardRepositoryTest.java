package com.sanedge.payment_gateway.repository.withdraw.statsbycard;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthlyAmount;
import com.sanedge.payment_gateway.models.withdraw.WithdrawYearlyAmount;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawAmountByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawAmountByCardRepository amountByCardRepository;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        Withdraw w1 = new Withdraw();
        w1.setWithdrawNo(UUID.randomUUID());
        w1.setCardNumber(cardNumber);
        w1.setWithdrawAmount(500000L);
        w1.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        w1.setStatus(Status.SUCCESS);
        commandRepository.save(w1);

        Withdraw w2 = new Withdraw();
        w2.setWithdrawNo(UUID.randomUUID());
        w2.setCardNumber("6543210987654321");
        w2.setWithdrawAmount(300000L);
        w2.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 20, 14, 0)));
        w2.setStatus(Status.SUCCESS);
        commandRepository.save(w2);
    }

    @Test
    void testFindMonthlyWithdrawsByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<WithdrawMonthlyAmount> results = amountByCardRepository.findMonthlyWithdrawsByCard(cardNumber, year);

        assertThat(results).hasSize(12);

        WithdrawMonthlyAmount april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyWithdrawsByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<WithdrawYearlyAmount> results = amountByCardRepository.findYearlyWithdrawsByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        WithdrawYearlyAmount current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }
}