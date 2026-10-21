package com.sanedge.payment_gateway.repository.saldo;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.saldo.Saldo;

public class SaldoRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SaldoQueryRepository saldoQueryRepository;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        saldoCommandRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindSaldo() {
        Saldo saldo = new Saldo();
        saldo.setCardNumber("9999888877776666");
        saldo.setTotalBalance(5000L);
        saldoCommandRepository.save(saldo);

        Optional<Saldo> found = saldoQueryRepository.findByCardNumber("9999888877776666");
        assertThat(found).isPresent();
        assertThat(found.get().getTotalBalance()).isEqualTo(5000L);
    }
}