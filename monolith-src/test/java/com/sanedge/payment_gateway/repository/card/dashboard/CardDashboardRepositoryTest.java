package com.sanedge.payment_gateway.repository.card.dashboard;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class CardDashboardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardDashboardRepository cardDashboardRepository;

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
    void shouldGetTotalBalance() {
        Long totalBalance = cardDashboardRepository.getTotalBalance();
        // BaseIntegrationTest seeds adminCard (1M) and regularCard (1M)
        // So total should be 2M
        assertThat(totalBalance).isEqualTo(2000000L);
    }

    @Test
    void shouldGetTotalTopupAmount() {
        Long amount = cardDashboardRepository.getTotalTopupAmount();
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalWithdrawAmount() {
        Long amount = cardDashboardRepository.getTotalWithdrawAmount();
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalTransactionAmount() {
        Long amount = cardDashboardRepository.getTotalTransactionAmount();
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalTransferAmount() {
        Long amount = cardDashboardRepository.getTotalTransferAmount();
        assertThat(amount).isNotNull();
    }
}