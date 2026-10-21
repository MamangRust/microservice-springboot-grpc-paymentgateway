package com.sanedge.payment_gateway.repository.saldo.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.saldo.SaldoMonthTotalBalance;
import com.sanedge.payment_gateway.models.saldo.SaldoYearTotalBalance;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;

public class SaldoTotalAmountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SaldoTotalAmountRepository saldoTotalAmountRepository;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        saldoCommandRepository.deleteAll();

        Saldo saldo = new Saldo();
        saldo.setCardNumber("1234567890");
        saldo.setTotalBalance(500000L);
        saldoCommandRepository.save(savedSaldoFrom(saldo));
    }

    private Saldo savedSaldoFrom(Saldo saldo) {
        return saldoCommandRepository.save(saldo);
    }

    @Test
    void shouldFindMonthlyTotalSaldoBalance() {
        int year = 2026;
        List<SaldoMonthTotalBalance> results = saldoTotalAmountRepository.findMonthlyTotalSaldoBalance((long) year, 1, (long) year, 12);
        assertThat(results).hasSize(12);
        SaldoMonthTotalBalance april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalBalance()).isEqualTo(500000L);
    }

    @Test
    void shouldFindYearlyTotalSaldoBalance() {
        List<SaldoYearTotalBalance> results = saldoTotalAmountRepository.findYearlyTotalSaldoBalance(2026L);
        assertThat(results).hasSize(2); // Current and previous;
        SaldoYearTotalBalance current = results.stream()
                .filter(r -> r.getReportYear().equals("2026"))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalBalance()).isEqualTo(500000L);
    }
}