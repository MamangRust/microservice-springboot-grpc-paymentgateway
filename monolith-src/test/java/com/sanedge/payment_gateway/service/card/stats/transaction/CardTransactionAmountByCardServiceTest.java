package com.sanedge.payment_gateway.service.card.stats.transaction;

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
import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class CardTransactionAmountByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardTransactionAmountByCardService cardTransactionAmountByCardService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser, adminCard, adminMerchant are pre-seeded by BaseIntegrationTest

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber(adminCard.getCardNumber());
        tx.setMerchantId(adminMerchant.getMerchantId());
        tx.setAmount(100000L);
        tx.setPaymentMethod("CARD");
        tx.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        tx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(tx);

        entityManager.flush();
    }

    @Test
    void shouldFindMonthAmountByCard() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        ApiResponse<List<CardResponseMonthAmount>> response = cardTransactionAmountByCardService.findMonthAmountByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearAmountByCard() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        var response = cardTransactionAmountByCardService.findYearAmountByCard(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}