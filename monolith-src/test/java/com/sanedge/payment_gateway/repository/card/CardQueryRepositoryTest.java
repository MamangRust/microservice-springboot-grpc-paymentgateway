package com.sanedge.payment_gateway.repository.card;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.card.Card;

public class CardQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardQueryRepository cardQueryRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser and adminCard are pre-seeded by BaseIntegrationTest
        entityManager.flush();
    }

    @Test
    void shouldFindByCardNumber() {
        Optional<Card> card = cardQueryRepository.findCardByCardNumber(adminCard.getCardNumber());
        assertThat(card).isPresent();
    }

    @Test
    void shouldFindCards() {
        var page = cardQueryRepository.findCards("VISA", org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void shouldFindActiveCards() {
        var page = cardQueryRepository.findActiveCards(null, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void shouldFindTrashedCards() {
        Card card = cardQueryRepository.findAll().get(0);
        cardCommandRepository.trashed(card.getCardId());

        var page = cardQueryRepository.findTrashedCards(null, org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void shouldFindCardById() {
        Card card = cardQueryRepository.findAll().get(0);
        Optional<Card> found = cardQueryRepository.findCardById(card.getCardId());
        assertThat(found).isPresent();
    }

    @Test
    void shouldFindCardByUserId() {
        Optional<Card> found = cardQueryRepository.findCardByUserId(adminUser.getUserId());
        assertThat(found).isPresent();
    }
}