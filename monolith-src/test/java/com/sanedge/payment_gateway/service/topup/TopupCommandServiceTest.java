package com.sanedge.payment_gateway.service.topup;

        import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import java.sql.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.topup.CreateTopupRequest;
import com.sanedge.payment_gateway.domain.requests.topup.UpdateTopupRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserCommandRepository;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.models.saldo.Saldo;

public class TopupCommandServiceTest extends BaseIntegrationTest {

    @Autowired
    private TopupCommandService topupCommandService;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @Autowired
    private UserCommandRepository userCommandRepository;

    @Autowired
    private CardCommandRepository cardCommandRepository;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        com.sanedge.payment_gateway.models.User user = new com.sanedge.payment_gateway.models.User();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        user.setUsername("u_" + uniqueId);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("t_" + uniqueId + "@example.com");
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

        Saldo saldo = new Saldo();
        saldo.setCardNumber("1234567890");
        saldo.setTotalBalance(100000L);
        saldoCommandRepository.save(saldo);
    }

    @Test
    void shouldCreateTopup() {
        CreateTopupRequest req = new CreateTopupRequest();
        req.setCardNumber("1234567890");
        req.setTopupAmount(100000L);
        req.setTopupMethod("VA_BCA");

        ApiResponse<TopupResponse> response = topupCommandService.create(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldUpdateTopup() {
        CreateTopupRequest createReq = new CreateTopupRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTopupAmount(100000L);
        createReq.setTopupMethod("VA_BCA");
        ApiResponse<TopupResponse> saved = topupCommandService.create(createReq);

        UpdateTopupRequest updateReq = new UpdateTopupRequest();
        updateReq.setTopupId(saved.getData().getId());
        updateReq.setCardNumber("1234567890");
        updateReq.setTopupAmount(150000L);
        updateReq.setTopupMethod("VA_BCA");

        ApiResponse<TopupResponse> response = topupCommandService.update(updateReq);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getTopupAmount()).isEqualTo(150000L);
    }

    @Test
    void shouldTrashTopup() {
        CreateTopupRequest createReq = new CreateTopupRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTopupAmount(100000L);
        createReq.setTopupMethod("VA_BCA");
        ApiResponse<TopupResponse> saved = topupCommandService.create(createReq);

        ApiResponse<TopupResponseDeleteAt> response = topupCommandService.trashed(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldRestoreTopup() {
        CreateTopupRequest createReq = new CreateTopupRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTopupAmount(100000L);
        createReq.setTopupMethod("VA_BCA");
        ApiResponse<TopupResponse> saved = topupCommandService.create(createReq);
        topupCommandService.trashed(saved.getData().getId());

        ApiResponse<TopupResponseDeleteAt> response = topupCommandService.restore(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeletePermanent() {
        CreateTopupRequest createReq = new CreateTopupRequest();
        createReq.setCardNumber("1234567890");
        createReq.setTopupAmount(100000L);
        createReq.setTopupMethod("VA_BCA");
        ApiResponse<TopupResponse> saved = topupCommandService.create(createReq);

        ApiResponse<Boolean> response = topupCommandService.deletePermanent(saved.getData().getId());
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isTrue();
    }

    @Test
    void shouldRestoreAll() {
        ApiResponse<Boolean> response = topupCommandService.restoreAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldDeleteAll() {
        ApiResponse<Boolean> response = topupCommandService.deleteAll();
        assertThat(response.getStatus()).isEqualTo("success");
    }
}