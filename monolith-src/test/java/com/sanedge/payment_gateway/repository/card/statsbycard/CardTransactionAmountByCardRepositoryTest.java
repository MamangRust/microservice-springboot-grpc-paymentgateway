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
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class CardTransactionAmountByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardTransactionAmountByCardRepository cardTransactionAmountByCardRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @BeforeEach
    void setUp() {
        Card card = new Card();
        card.setUserId(adminUser.getUserId());
        card.setCardNumber("9999222233336666");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        cardCommandRepository.save(card);

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber("9999222233336666");
        tx.setMerchantId(adminMerchant.getMerchantId());
        tx.setAmount(100000L);
        tx.setPaymentMethod("CARD");
        tx.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        tx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(tx);
    }

    @Test
    void shouldGetMonthlyTransactionAmountByCard() {
        List<CardMonthAmount> stats = cardTransactionAmountByCardRepository.getMonthlyTransactionAmountByCard("9999222233336666", LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetYearlyTransactionAmountByCard() {
        var stats = cardTransactionAmountByCardRepository.getYearlyTransactionAmountByCard("9999222233336666", (long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }
}