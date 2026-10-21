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
import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class CardTransferAmountByCardServiceTest extends BaseIntegrationTest {

    @Autowired
    private CardTransferAmountByCardService cardTransferAmountByCardService;

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
    void shouldFindMonthAmountSender() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        ApiResponse<List<CardResponseMonthAmount>> response = cardTransferAmountByCardService.findMonthAmountSender(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindMonthAmountReceiver() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        var response = cardTransferAmountByCardService.findMonthAmountReceiver(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearAmountSender() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        var response = cardTransferAmountByCardService.findYearAmountSender(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearAmountReceiver() {
        MonthYearCardNumberCard req = new MonthYearCardNumberCard(adminCard.getCardNumber(), (long) LocalDate.now().getYear());
        var response = cardTransferAmountByCardService.findYearAmountReceiver(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}