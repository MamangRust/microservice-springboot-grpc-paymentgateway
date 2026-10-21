package com.sanedge.payment_gateway.repository.card.stats;

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
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.models.saldo.Saldo;

public class CardBalanceRepositoryTest extends BaseIntegrationTest {

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
    void shouldCalculateMonthlyBalances() {
        List<CardMonthBalance> stats = cardBalanceRepository.getMonthlyBalances(LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldCalculateYearlyBalances() {
        var stats = cardBalanceRepository.getYearlyBalances((long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }
}