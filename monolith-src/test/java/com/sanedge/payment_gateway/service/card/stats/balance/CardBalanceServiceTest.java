package com.sanedge.payment_gateway.service.card.stats.balance;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class CardBalanceServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardBalanceService cardBalanceService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser, adminCard, adminSaldo are pre-seeded by BaseIntegrationTest
        entityManager.flush();
    }

    @Test
    void shouldGetMonthBalance() {
        ApiResponse<List<CardResponseMonthBalance>> response = cardBalanceService.findMonthBalance((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetYearBalance() {
        var response = cardBalanceService.findYearBalance((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}