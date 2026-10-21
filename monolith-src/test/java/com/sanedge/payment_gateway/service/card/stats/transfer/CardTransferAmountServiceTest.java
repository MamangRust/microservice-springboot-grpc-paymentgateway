package com.sanedge.payment_gateway.service.card.stats.transfer;

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
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class CardTransferAmountServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardTransferAmountService cardTransferAmountService;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private TransferCommandRepository transferCommandRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // adminUser, regularUser, adminCard, regularCard are pre-seeded by BaseIntegrationTest

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
    void shouldGetMonthAmountSender() {
        ApiResponse<List<CardResponseMonthAmount>> response = cardTransferAmountService.findMonthAmountSender((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetMonthAmountReceiver() {
        var response = cardTransferAmountService.findMonthAmountReceiver((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetYearAmountSender() {
        var response = cardTransferAmountService.findYearAmountSender((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetYearAmountReceiver() {
        var response = cardTransferAmountService.findYearAmountReceiver((long) LocalDate.now().getYear());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}