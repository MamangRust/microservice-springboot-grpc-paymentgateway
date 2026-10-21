package com.sanedge.payment_gateway.service.card.stats.withdraw;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class CardWithdrawAmountByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardWithdrawAmountByCardService cardWithdrawAmountByCardService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private WithdrawCommandRepository withdrawCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser and adminCard are pre-seeded by BaseIntegrationTest

        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber(adminCard.getCardNumber());
        withdraw.setWithdrawAmount(20000L);
        withdraw.setWithdrawTime(new Timestamp(System.currentTimeMillis()));
        withdraw.setStatus(Status.SUCCESS);
        withdrawCommandRepository.save(withdraw);

        entityManager.flush();
    }

    @Test
    void shouldFindMonthAmountByCard() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        ApiResponse<List<CardResponseMonthAmount>> response = cardWithdrawAmountByCardService.findMonthAmountByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearAmountByCard() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        var response = cardWithdrawAmountByCardService.findYearAmountByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}