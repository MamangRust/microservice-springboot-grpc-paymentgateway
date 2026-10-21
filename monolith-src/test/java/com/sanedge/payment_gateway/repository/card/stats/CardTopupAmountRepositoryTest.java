package com.sanedge.payment_gateway.repository.card.stats;

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

public class CardTopupAmountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardTopupAmountRepository cardTopupAmountRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        Card card = new Card();
        card.setUserId(adminUser.getUserId());
        card.setCardNumber("9999222233334444");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        cardCommandRepository.save(card);

        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber("5555666677778888");
        topup.setTopupAmount(50000L);
        topup.setTopupMethod("BANK_TRANSFER");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup);
    }

    @Test
    void shouldGetMonthlyTopupAmount() {
        List<CardMonthAmount> stats = cardTopupAmountRepository.getMonthlyTopupAmount(LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetYearlyTopupAmount() {
        var stats = cardTopupAmountRepository.getYearlyTopupAmount((long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }
}