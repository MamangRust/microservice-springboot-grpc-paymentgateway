package com.sanedge.payment_gateway.service.card;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.card.FindAllCards;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;

public class CardQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardQueryService cardQueryService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminCard is pre-seeded by BaseIntegrationTest
        entityManager.flush();
    }

    @Test
    void shouldFindAllCards() {
        FindAllCards req = new FindAllCards();
        req.setPage(1);
        req.setPageSize(10);

        ApiResponsePagination<List<CardResponse>> response = cardQueryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void shouldFindByActive() {
        FindAllCards req = new FindAllCards();
        req.setPage(1);
        req.setPageSize(10);

        var response = cardQueryService.findByActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByTrashed() {
        Card card = cardCommandRepository.findAll().get(0);
        cardCommandRepository.trashed(card.getCardId());

        FindAllCards req = new FindAllCards();
        req.setPage(1);
        req.setPageSize(10);

        var response = cardQueryService.findByTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindById() {
        Card card = cardCommandRepository.findAll().get(0);
        var response = cardQueryService.findById(card.getCardId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByUserId() {
        var response = cardQueryService.findByUserId(adminUser.getUserId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByCardNumber() {
        var response = cardQueryService.findByCardNumber(adminCard.getCardNumber());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}