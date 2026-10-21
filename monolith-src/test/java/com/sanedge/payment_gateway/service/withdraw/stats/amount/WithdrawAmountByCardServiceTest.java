package com.sanedge.payment_gateway.service.withdraw.stats.amount;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearMonthCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawAmountByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawAmountByCardService amountByCardService;

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
    void testFindMonthlyByCardNumber() {
        Long year = (long) LocalDateTime.now().getYear();
        YearMonthCardNumber req = new YearMonthCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year.intValue());

        ApiResponse<List<WithdrawMonthlyAmountResponse>> response = amountByCardService.findMonthlyByCardNumber(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(12);

        WithdrawMonthlyAmountResponse april = response.getData().stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyByCardNumber() {
        Long year = (long) LocalDateTime.now().getYear();
        YearMonthCardNumber req = new YearMonthCardNumber();
        req.setCardNumber(cardNumber);
        req.setYear(year.intValue());

        ApiResponse<List<WithdrawYearlyAmountResponse>> response = amountByCardService.findYearlyByCardNumber(req);
        assertThat(response.getStatus()).isEqualTo("success");

        WithdrawYearlyAmountResponse current = response.getData().stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }
}