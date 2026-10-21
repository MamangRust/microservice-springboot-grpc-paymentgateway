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
import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthStatusFailed;
import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthStatusSuccess;
import com.sanedge.payment_gateway.models.withdraw.WithdrawYearStatusFailed;
import com.sanedge.payment_gateway.models.withdraw.WithdrawYearStatusSuccess;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawStatusByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawStatusByCardRepository statusByCardRepository;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        // Success w1
        Withdraw w1 = new Withdraw();
        w1.setWithdrawNo(UUID.randomUUID());
        w1.setCardNumber(cardNumber);
        w1.setWithdrawAmount(500000L);
        w1.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        w1.setStatus(Status.SUCCESS);
        commandRepository.save(w1);

        // Failed w2 (different card)
        Withdraw w2 = new Withdraw();
        w2.setWithdrawNo(UUID.randomUUID());
        w2.setCardNumber("6543210987654321");
        w2.setWithdrawAmount(300000L);
        w2.setWithdrawTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        w2.setStatus(Status.FAILED);
        commandRepository.save(w2);
    }

    @Test
    void testFindMonthWithdrawStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<WithdrawMonthStatusSuccess> results = statusByCardRepository.findMonthWithdrawStatusSuccessByCard(cardNumber, year, 1, year, 12);

        assertThat(results).isNotEmpty();

        WithdrawMonthStatusSuccess april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindMonthWithdrawStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<WithdrawMonthStatusFailed> results = statusByCardRepository.findMonthWithdrawStatusFailedByCard(cardNumber, year, 1, year, 12);

        assertThat(results).isNotEmpty();

        WithdrawMonthStatusFailed may = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(0L); // Should be 0 for this card;
    }

    @Test
    void testFindYearlyWithdrawStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<WithdrawYearStatusSuccess> results = statusByCardRepository.findYearlyWithdrawStatusSuccessByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        WithdrawYearStatusSuccess current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyWithdrawStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<WithdrawYearStatusFailed> results = statusByCardRepository.findYearlyWithdrawStatusFailedByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        WithdrawYearStatusFailed current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(0L);
    }
}