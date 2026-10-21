package com.sanedge.payment_gateway.repository.card.statsbycard;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.card.CardMonthBalance;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class CardBalanceByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardBalanceByCardRepository cardBalanceByCardRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminCard and adminSaldo are pre-seeded by BaseIntegrationTest
        entityManager.flush();
    }

    @Test
    void shouldGetMonthlyBalancesByCard() {
        List<CardMonthBalance> stats = cardBalanceByCardRepository.getMonthlyBalancesByCard(LocalDate.now(), adminCard.getCardNumber());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetYearlyBalancesByCard() {
        var stats = cardBalanceByCardRepository.getYearlyBalancesByCard((long) LocalDate.now().getYear(), adminCard.getCardNumber());
        assertThat(stats).isNotNull();
    }
}