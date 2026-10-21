package com.sanedge.payment_gateway.service.transfer;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transfers.CreateTransferRequest;
import com.sanedge.payment_gateway.domain.requests.transfers.UpdateTransferRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransferCommandService commandService;

    @Autowired
    private TransferCommandRepository commandRepository;

    private Long transferId;

    @Autowired
    private com.sanedge.payment_gateway.repository.card.CardCommandRepository cardCommandRepository;

    @Autowired
    private com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        // Use seeded data from BaseIntegrationTest
        String fromCard = regularCard.getCardNumber();
        String toCard = "555566667777" + UUID.randomUUID().toString().substring(0, 4);

        // c1 is already saved as regularCard in BaseIntegrationTest

        com.sanedge.payment_gateway.models.card.Card c2 = new com.sanedge.payment_gateway.models.card.Card();
        c2.setCardNumber(toCard);
        c2.setCardType("DEBIT");
        c2.setCardProvider("VISA");
        c2.setCvv("123");
        c2.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        c2.setUserId(regularUser.getUserId());
        cardCommandRepository.save(c2);

        // s1 is already saved in BaseIntegrationTest (regularSaldo)
        regularSaldo.setTotalBalance(1000000L);
        saldoCommandRepository.save(regularSaldo);

        com.sanedge.payment_gateway.models.saldo.Saldo s2 = new com.sanedge.payment_gateway.models.saldo.Saldo();
        s2.setCardNumber(toCard);
        s2.setTotalBalance(500000L);
        saldoCommandRepository.save(s2);

        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom(fromCard);
        transfer.setTransferTo(toCard);
        transfer.setTransferAmount(500000L);
        transfer.setTransferTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

        Transfer saved = commandRepository.save(transfer);
        transferId = saved.getTransferId();
    }

    @Test
    void testCreate() {
        CreateTransferRequest req = new CreateTransferRequest();
        req.setTransferFrom(regularCard.getCardNumber());
        req.setTransferTo("5555666677778888");
        req.setTransferAmount(150000L);

        ApiResponse<TransferResponse> response = commandService.create(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getTransferAmount()).isEqualTo(150000L);
    }

    @Test
    void testUpdate() {
        UpdateTransferRequest req = new UpdateTransferRequest();
        req.setTransferId(transferId);
        req.setTransferFrom(regularCard.getCardNumber());
        req.setTransferTo("5555666677778888");
        req.setTransferAmount(200000L);

        ApiResponse<TransferResponse> response = commandService.update(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getTransferAmount()).isEqualTo(200000L);
    }

    @Test
    void testTrashed() {
        ApiResponse<TransferResponseDeleteAt> response = commandService.trashed(transferId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getDeletedAt()).isNotNull();
    }

    @Test
    void testRestore() {
        commandService.trashed(transferId);
        ApiResponse<TransferResponseDeleteAt> response = commandService.restore(transferId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getDeletedAt()).isNull();
    }

    @Test
    void testDeletePermanent() {
        commandService.trashed(transferId);
        ApiResponse<Boolean> response = commandService.deletePermanent(transferId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void testRestoreAll() {
        commandService.trashed(transferId);
        ApiResponse<Boolean> response = commandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void testDeleteAll() {
        commandService.trashed(transferId);
        ApiResponse<Boolean> response = commandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }
}