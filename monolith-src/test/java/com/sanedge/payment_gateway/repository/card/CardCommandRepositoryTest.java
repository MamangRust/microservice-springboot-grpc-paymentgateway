package com.sanedge.payment_gateway.repository.card;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.card.Card;

public class CardCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        entityManager.flush();
    }

    @Test
    void shouldSaveCard() {
        Card card = new Card();
        card.setUserId(adminUser.getUserId());
        card.setCardNumber("2222333344445555");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        Card saved = cardCommandRepository.save(card);
        assertThat(saved.getCardId()).isNotNull();
    }

    @Test
    void shouldTrashedCard() {
        Card card = new Card();
        card.setUserId(adminUser.getUserId());
        card.setCardNumber("2222333344445555");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        card = cardCommandRepository.save(card);

        Card trashed = cardCommandRepository.trashed(card.getCardId());
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldRestoreCard() {
        Card card = new Card();
        card.setUserId(adminUser.getUserId());
        card.setCardNumber("2222333344445555");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        card = cardCommandRepository.save(card);

        cardCommandRepository.trashed(card.getCardId());
        Card restored = cardCommandRepository.restore(card.getCardId());
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeletePermanent() {
        Card card = new Card();
        card.setUserId(adminUser.getUserId());
        card.setCardNumber("2222333344445555");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        card = cardCommandRepository.save(card);

        boolean deleted = cardCommandRepository.deletePermanent(card.getCardId());
        assertThat(deleted).isTrue();
        assertThat(cardCommandRepository.findById(card.getCardId())).isEmpty();
    }
}