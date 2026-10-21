package com.sanedge.payment_gateway.service.card;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboard;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class CardDashboardServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardDashboardService cardDashboardService;

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
    void shouldGetDashboard() {
        ApiResponse<CardDashboard> response = cardDashboardService.dashboard();
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetDashboardByCard() {
        var response = cardDashboardService.dashboardByCard(adminCard.getCardNumber());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}