package com.sanedge.payment_gateway.service.saldo;

        import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import java.sql.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.saldo.CreateSaldoRequest;
import com.sanedge.payment_gateway.domain.requests.saldo.UpdateSaldoRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;

public class SaldoCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private SaldoCommandService saldoCommandService;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @BeforeEach
    void setUp() {
        saldoCommandRepository.deleteAll();
        cardCommandRepository.deleteAll();
        userCommandRepository.deleteAll();

        com.sanedge.payment_gateway.models.User user = new com.sanedge.payment_gateway.models.User();
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user = userCommandRepository.save(user);

        com.sanedge.payment_gateway.models.card.Card card = new com.sanedge.payment_gateway.models.card.Card();
        card.setCardNumber("1234567890");
        card.setCardProvider("VISA");
        card.setCardType("DEBIT");
        card.setExpireDate(Date.valueOf("2025-12-31"));
        card.setCvv("123");
        card.setUserId(user.getUserId());
        cardCommandRepository.save(card);
    }

    @Test
    void shouldCreateSaldo() {
        CreateSaldoRequest req = new CreateSaldoRequest();
        req.setCardNumber("1234567890");
        req.setTotalBalance(100000L);

        ApiResponse<SaldoResponse> response = saldoCommandService.create(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldUpdateSaldo() {
        CreateSaldoRequest createReq = new CreateSaldoRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTotalBalance(100000L);
        ApiResponse<SaldoResponse> saved = saldoCommandService.create(createReq);

        UpdateSaldoRequest updateReq = new UpdateSaldoRequest();
        updateReq.setSaldoId(saved.getData().getId());
        updateReq.setCardNumber("1234567890");
        updateReq.setTotalBalance(150000L);

        ApiResponse<SaldoResponse> response = saldoCommandService.update(updateReq);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getTotalBalance()).isEqualTo(150000L);
    }

    @Test
    void shouldTrashSaldo() {
        CreateSaldoRequest createReq = new CreateSaldoRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTotalBalance(100000L);
        ApiResponse<SaldoResponse> saved = saldoCommandService.create(createReq);

        ApiResponse<SaldoResponseDeleteAt> response = saldoCommandService.trash(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldRestoreSaldo() {
        CreateSaldoRequest createReq = new CreateSaldoRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTotalBalance(100000L);
        ApiResponse<SaldoResponse> saved = saldoCommandService.create(createReq);
        saldoCommandService.trash(saved.getData().getId());

        ApiResponse<SaldoResponseDeleteAt> response = saldoCommandService.restore(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteSaldo() {
        CreateSaldoRequest createReq = new CreateSaldoRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTotalBalance(100000L);
        ApiResponse<SaldoResponse> saved = saldoCommandService.create(createReq);

        ApiResponse<Boolean> response = saldoCommandService.delete(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void shouldRestoreAll() {
        ApiResponse<Boolean> response = saldoCommandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteAll() {
        ApiResponse<Boolean> response = saldoCommandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }
}