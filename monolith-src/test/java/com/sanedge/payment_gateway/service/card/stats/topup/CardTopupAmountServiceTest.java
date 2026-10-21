package com.sanedge.payment_gateway.service.card.stats.topup;

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
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class CardTopupAmountServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardTopupAmountService cardTopupAmountService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser and adminCard are pre-seeded by BaseIntegrationTest

        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber(adminCard.getCardNumber());
        topup.setTopupAmount(50000L);
        topup.setTopupMethod("BANK_TRANSFER");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);

        entityManager.flush();
    }

    @Test
    void shouldGetMonthAmount() {
        ApiResponse<List<CardResponseMonthAmount>> response = cardTopupAmountService.findMonthAmount((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetYearAmount() {
        var response = cardTopupAmountService.findYearAmount((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}