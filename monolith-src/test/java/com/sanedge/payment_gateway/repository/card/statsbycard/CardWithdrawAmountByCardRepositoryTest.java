package com.sanedge.payment_gateway.repository.card.statsbycard;

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
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.card.CardMonthAmount;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class CardWithdrawAmountByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardWithdrawAmountByCardRepository cardWithdrawAmountByCardRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private WithdrawCommandRepository withdrawCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminCard is pre-seeded by BaseIntegrationTest

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
    void shouldGetMonthlyWithdrawAmountByCard() {
        List<CardMonthAmount> stats = cardWithdrawAmountByCardRepository.getMonthlyWithdrawAmountByCard(adminCard.getCardNumber(), LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetYearlyWithdrawAmountByCard() {
        var stats = cardWithdrawAmountByCardRepository.getYearlyWithdrawAmountByCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }
}