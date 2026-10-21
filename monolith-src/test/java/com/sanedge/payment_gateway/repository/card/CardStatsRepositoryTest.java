package com.sanedge.payment_gateway.repository.card;

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
import com.sanedge.payment_gateway.models.card.CardYearBalance;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.card.stats.CardBalanceRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class CardStatsRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardBalanceRepository cardBalanceRepository;

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
    void shouldGetMonthlyBalances() {
        List<CardMonthBalance> stats = cardBalanceRepository.getMonthlyBalances(LocalDate.now());
        assertThat(stats).isNotEmpty();
        // Since we saved one saldo today, at least one month should have data
        assertThat(stats.stream().anyMatch(s -> s.getTotalBalance() > 0)).isTrue();
    }

    @Test
    void shouldGetYearlyBalances() {
        List<CardYearBalance> stats = cardBalanceRepository.getYearlyBalances((long) LocalDate.now().getYear());
        assertThat(stats).isNotEmpty();
        assertThat(stats.stream().anyMatch(s -> s.getTotalBalance() > 0)).isTrue();
    }
}