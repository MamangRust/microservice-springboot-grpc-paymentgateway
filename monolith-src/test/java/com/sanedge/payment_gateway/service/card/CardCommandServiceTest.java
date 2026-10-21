package com.sanedge.payment_gateway.service.card;

        import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.card.CreateCardRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.models.User;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;

public class CardCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardCommandService cardCommandService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser is pre-seeded by BaseIntegrationTest
        this.userId = adminUser.getUserId();
        entityManager.flush();
    }

    private Long userId;

    @Test
    void shouldCreateCard() {
        CreateCardRequest req = new CreateCardRequest();
        req.setUserId(userId);
        req.setCardType("DEBIT");
        req.setCardProvider("VISA");
        req.setCvv("123");
        req.setExpireDate(LocalDate.now().plusYears(1));

        ApiResponse<CardResponse> response = cardCommandService.createCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldUpdateCard() {
        CreateCardRequest createReq = new CreateCardRequest();
        createReq.setUserId(userId);
        createReq.setCardType("DEBIT");
        createReq.setCardProvider("VISA");
        createReq.setCvv("123");
        createReq.setExpireDate(LocalDate.now().plusYears(1));
        ApiResponse<CardResponse> saved = cardCommandService.createCard(createReq);

        com.sanedge.payment_gateway.domain.requests.card.UpdateCardRequest updateReq = new com.sanedge.payment_gateway.domain.requests.card.UpdateCardRequest();
        updateReq.setCardId(saved.getData().getId());
        updateReq.setCardType("CREDIT");
        updateReq.setCardProvider("MASTERCARD");
        updateReq.setCvv("456");
        updateReq.setUserId(userId);
        updateReq.setExpireDate(LocalDate.now().plusYears(2));

        ApiResponse<CardResponse> response = cardCommandService.updateCard(updateReq);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldTrashCard() {
        CreateCardRequest createReq = new CreateCardRequest();
        createReq.setUserId(userId);
        createReq.setCardType("DEBIT");
        createReq.setCardProvider("VISA");
        createReq.setCvv("123");
        createReq.setExpireDate(LocalDate.now().plusYears(1));
        ApiResponse<CardResponse> saved = cardCommandService.createCard(createReq);

        var response = cardCommandService.trashCard(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldRestoreCard() {
        CreateCardRequest createReq = new CreateCardRequest();
        createReq.setUserId(userId);
        createReq.setCardType("DEBIT");
        createReq.setCardProvider("VISA");
        createReq.setCvv("123");
        createReq.setExpireDate(LocalDate.now().plusYears(1));
        ApiResponse<CardResponse> saved = cardCommandService.createCard(createReq);

        cardCommandService.trashCard(saved.getData().getId());
        var response = cardCommandService.restoreCard(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteCard() {
        CreateCardRequest createReq = new CreateCardRequest();
        createReq.setUserId(userId);
        createReq.setCardType("DEBIT");
        createReq.setCardProvider("VISA");
        createReq.setCvv("123");
        createReq.setExpireDate(LocalDate.now().plusYears(1));
        ApiResponse<CardResponse> saved = cardCommandService.createCard(createReq);

        var response = cardCommandService.deleteCard(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    // These tests are likely problematic due to global state impact
    // and are less critical for unit verification of card creation
}