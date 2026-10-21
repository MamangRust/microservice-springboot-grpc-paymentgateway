package com.sanedge.payment_gateway.repository.saldo.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.saldo.SaldoMonthBalance;
import com.sanedge.payment_gateway.models.saldo.SaldoYearBalance;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class SaldoBalanceRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SaldoBalanceRepository saldoBalanceRepository;

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
    void shouldFindMonthlySaldoBalances() {
        List<SaldoMonthBalance> results = saldoBalanceRepository.findMonthlySaldoBalances(2026L);
        assertThat(results).hasSize(12);
        // Find current month (April is Apr)
        SaldoMonthBalance april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalBalance()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    void shouldFindYearlySaldoBalances() {
        List<SaldoYearBalance> results = saldoBalanceRepository.findYearlySaldoBalances(2026L);
        assertThat(results).isNotEmpty();
        SaldoYearBalance year2026 = results.stream()
                .filter(r -> r.getReportYear().equals("2026"))
                .findFirst()
                .orElseThrow();
        assertThat(year2026.getTotalBalance()).isEqualTo(100000L);
    }
}