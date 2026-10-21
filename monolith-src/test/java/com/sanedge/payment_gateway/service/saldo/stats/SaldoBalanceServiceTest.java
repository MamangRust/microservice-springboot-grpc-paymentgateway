package com.sanedge.payment_gateway.service.saldo.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoYearBalanceResponse;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class SaldoBalanceServiceTest extends BaseIntegrationTest {

    @Autowired
    private SaldoBalanceService saldoBalanceService;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        saldoCommandRepository.deleteAll();

        Saldo saldo = new Saldo();
        saldo.setCardNumber("1234567890");
        saldo.setTotalBalance(500000L);
        saldoCommandRepository.save(saldo);
    }

    @Test
    void shouldGetMonthBalance() {
        ApiResponse<List<SaldoMonthBalanceResponse>> response = saldoBalanceService.getMonthBalance(2026L);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldGetYearBalance() {
        ApiResponse<List<SaldoYearBalanceResponse>> response = saldoBalanceService.getYearBalance(2026L);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}