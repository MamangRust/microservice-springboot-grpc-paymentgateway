package com.sanedge.payment_gateway.service.saldo.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.saldo.MonthTotalSaldoBalance;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoYearTotalBalanceResponse;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class SaldoTotalAmountServiceTest extends BaseIntegrationTest {

    @Autowired
    private SaldoTotalAmountService saldoTotalAmountService;

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
    void shouldFindMonthTotalBalance() {
        MonthTotalSaldoBalance req = new MonthTotalSaldoBalance();
        req.setYear(2026L);
        req.setMonth(4);
        ApiResponse<List<SaldoMonthTotalBalanceResponse>> response = saldoTotalAmountService.findMonthTotalBalance(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindYearTotalBalance() {
        ApiResponse<List<SaldoYearTotalBalanceResponse>> response = saldoTotalAmountService.findYearTotalBalance(2026L);
        assertThat(response.getStatus()).isEqualTo("success");
    }
}