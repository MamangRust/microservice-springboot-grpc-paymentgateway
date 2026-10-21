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
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class CardTransferAmountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CardTransferAmountRepository cardTransferAmountRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TransferCommandRepository transferCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminCard and regularCard are pre-seeded by BaseIntegrationTest

        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom(adminCard.getCardNumber());
        transfer.setTransferTo(regularCard.getCardNumber());
        transfer.setTransferAmount(50000L);
        transfer.setTransferTime(new Timestamp(System.currentTimeMillis()));
        transfer.setStatus(Status.SUCCESS);
        transferCommandRepository.save(transfer);

        entityManager.flush();
    }

    @Test
    void shouldGetMonthlyTransferAmountSender() {
        List<CardMonthAmount> stats = cardTransferAmountRepository.getMonthlyTransferAmountSender(LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetMonthlyTransferAmountReceiver() {
        List<CardMonthAmount> stats = cardTransferAmountRepository.getMonthlyTransferAmountReceiver(LocalDate.now());
        assertThat(stats).isNotEmpty();
    }

    @Test
    void shouldGetYearlyTransferAmountSender() {
        var stats = cardTransferAmountRepository.getYearlyTransferAmountSender((long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }

    @Test
    void shouldGetYearlyTransferAmountReceiver() {
        var stats = cardTransferAmountRepository.getYearlyTransferAmountReceiver((long) LocalDate.now().getYear());
        assertThat(stats).isNotNull();
    }
}