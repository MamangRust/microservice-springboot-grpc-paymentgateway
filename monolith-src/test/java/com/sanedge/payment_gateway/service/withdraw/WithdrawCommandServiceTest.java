package com.sanedge.payment_gateway.service.withdraw;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.withdraws.CreateWithdrawRequest;
import com.sanedge.payment_gateway.domain.requests.withdraws.UpdateWithdrawRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawCommandService commandService;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private Long withdrawId;

    @Autowired
    private com.sanedge.payment_gateway.repository.card.CardCommandRepository cardCommandRepository;

    @Autowired
    private com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        // Seed Card
        com.sanedge.payment_gateway.models.card.Card card = new com.sanedge.payment_gateway.models.card.Card();
        card.setCardNumber("1234567890123456");
        card.setCardType("DEBIT");
        card.setCardProvider("VISA");
        card.setCvv("123");
        card.setExpireDate(Date.valueOf(LocalDate.now().plusYears(1)));
        card.setUserId(this.adminUser.getUserId());
        card = cardCommandRepository.save(card);

        // Seed Saldo
        com.sanedge.payment_gateway.models.saldo.Saldo saldo = new com.sanedge.payment_gateway.models.saldo.Saldo();
        saldo.setCardNumber(card.getCardNumber());
        saldo.setTotalBalance(1000000L);
        saldoCommandRepository.save(saldo);

        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber(card.getCardNumber());
        withdraw.setWithdrawAmount(500000L);
        withdraw.setWithdrawTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

        Withdraw saved = commandRepository.save(withdraw);
        withdrawId = saved.getWithdrawId();
    }

    @Test
    void testCreate() {
        CreateWithdrawRequest req = new CreateWithdrawRequest();
        req.setCardNumber("1234567890123456");
        req.setWithdrawAmount(150000L);

        ApiResponse<WithdrawResponse> response = commandService.create(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getWithdrawAmount()).isEqualTo(150000L);
    }

    @Test
    void testUpdate() {
        UpdateWithdrawRequest req = new UpdateWithdrawRequest();
        req.setWithdrawId(withdrawId);
        req.setCardNumber("1234567890123456");
        req.setWithdrawAmount(200000L);

        ApiResponse<WithdrawResponse> response = commandService.update(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getWithdrawAmount()).isEqualTo(200000L);
    }

    @Test
    void testTrashed() {
        ApiResponse<WithdrawResponseDeleteAt> response = commandService.trashed(withdrawId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getDeletedAt()).isNotNull();
    }

    @Test
    void testRestore() {
        commandService.trashed(withdrawId);
        ApiResponse<WithdrawResponseDeleteAt> response = commandService.restore(withdrawId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getDeletedAt()).isNull();
    }

    @Test
    void testDeletePermanent() {
        commandService.trashed(withdrawId);
        ApiResponse<Boolean> response = commandService.deletePermanent(withdrawId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void testRestoreAll() {
        commandService.trashed(withdrawId);
        ApiResponse<Boolean> response = commandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void testDeleteAll() {
        commandService.trashed(withdrawId);
        ApiResponse<Boolean> response = commandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }
}