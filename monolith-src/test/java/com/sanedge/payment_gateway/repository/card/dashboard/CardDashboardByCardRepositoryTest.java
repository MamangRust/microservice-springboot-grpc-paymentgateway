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

public class CardDashboardByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardDashboardByCardRepository cardDashboardByCardRepository;

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
    void shouldGetTotalBalanceByCard() {
        Long balance = cardDashboardByCardRepository.getTotalBalanceByCard(adminCard.getCardNumber());
        assertThat(balance).isEqualTo(1000000L);
    }

    @Test
    void shouldGetTotalTopupAmountByCard() {
        Long amount = cardDashboardByCardRepository.getTotalTopupAmountByCard(adminCard.getCardNumber());
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalWithdrawAmountByCard() {
        Long amount = cardDashboardByCardRepository.getTotalWithdrawAmountByCard(adminCard.getCardNumber());
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalTransactionAmountByCard() {
        Long amount = cardDashboardByCardRepository.getTotalTransactionAmountByCard(adminCard.getCardNumber());
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalTransferAmountBySender() {
        Long amount = cardDashboardByCardRepository.getTotalTransferAmountBySender(adminCard.getCardNumber());
        assertThat(amount).isNotNull();
    }

    @Test
    void shouldGetTotalTransferAmountByReceiver() {
        Long amount = cardDashboardByCardRepository.getTotalTransferAmountByReceiver(adminCard.getCardNumber());
        assertThat(amount).isNotNull();
    }
}