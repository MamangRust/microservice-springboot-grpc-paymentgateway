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
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class CardTopupAmountByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardTopupAmountByCardRepository cardTopupAmountByCardRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        // No destructive cleanup
        String cardNumber = adminCard.getCardNumber();

        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber(cardNumber);
        topup.setTopupAmount(50000L);
        topup.setTopupMethod("BANK_TRANSFER");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);
    }

    @Test
    void shouldGetMonthlyTopupAmountByCard() {
        List<CardMonthAmount> stats = cardTopupAmountByCardRepository.getMonthlyTopupAmountByCard(adminCard.getCardNumber(), LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetYearlyTopupAmountByCard() {
        var stats = cardTopupAmountByCardRepository.getYearlyTopupAmountByCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }
}