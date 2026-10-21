package com.sanedge.payment_gateway.service.saldo;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.saldo.FindAllSaldos;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.SaldoResponseDeleteAt;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class SaldoQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private SaldoQueryService saldoQueryService;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        saldoCommandRepository.deleteAll();

        Saldo saldo = new Saldo();
        saldo.setCardNumber("1234567890");
        saldo.setTotalBalance(100000L);
        saldoCommandRepository.save(saldo);
    }

    @Test
    void shouldFindAll() {
        FindAllSaldos req = new FindAllSaldos();
        req.setPage(1);
        req.setPageSize(10);
        ApiResponsePagination<List<SaldoResponse>> response = saldoQueryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void shouldFindActive() {
        FindAllSaldos req = new FindAllSaldos();
        ApiResponsePagination<List<SaldoResponseDeleteAt>> response = saldoQueryService.findActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindTrashed() {
        FindAllSaldos req = new FindAllSaldos();
        ApiResponsePagination<List<SaldoResponseDeleteAt>> response = saldoQueryService.findTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByCard() {
        ApiResponse<SaldoResponse> response = saldoQueryService.findByCard("1234567890");
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getCardNumber()).isEqualTo("1234567890");
    }

    @Test
    void shouldFindById() {
        Saldo saldo = saldoCommandRepository.findAll().get(0);
        ApiResponse<SaldoResponse> response = saldoQueryService.findById(saldo.getSaldoId());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}