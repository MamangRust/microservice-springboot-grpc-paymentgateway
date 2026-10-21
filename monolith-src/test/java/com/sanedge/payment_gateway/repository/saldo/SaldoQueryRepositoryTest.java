package com.sanedge.payment_gateway.repository.saldo;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.saldo.Saldo;

public class SaldoQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SaldoQueryRepository saldoQueryRepository;

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    @BeforeEach
    void setUp() {
        saldoCommandRepository.deleteAll();

        Saldo saldo1 = new Saldo();
        saldo1.setCardNumber("1234567890");
        saldo1.setTotalBalance(100000L);
        saldoCommandRepository.save(saldo1);

        Saldo saldo2 = new Saldo();
        saldo2.setCardNumber("0987654321");
        saldo2.setTotalBalance(200000L);
        saldoCommandRepository.save(saldo2);
    }

    @Test
    void shouldFindSaldos() {
        Page<Saldo> saldos = saldoQueryRepository.findSaldos("1234", PageRequest.of(0, 10));
        assertThat(saldos.getContent()).hasSize(1);
        assertThat(saldos.getContent().get(0).getCardNumber()).isEqualTo("1234567890");
    }

    @Test
    void shouldFindByCardNumber() {
        Optional<Saldo> saldo = saldoQueryRepository.findByCardNumber("0987654321");
        assertThat(saldo).isPresent();
        assertThat(saldo.get().getTotalBalance()).isEqualTo(200000L);
    }

    @Test
    void shouldFindActiveSaldos() {
        Page<Saldo> saldos = saldoQueryRepository.findActiveSaldos(null, PageRequest.of(0, 10));
        assertThat(saldos.getContent()).hasSize(2);
    }

    @Test
    void shouldFindTrashedSaldos() {
        Saldo saldo = saldoQueryRepository.findByCardNumber("1234567890").orElseThrow();
        saldoCommandRepository.trashed(saldo.getSaldoId());

        Page<Saldo> saldos = saldoQueryRepository.findTrashedSaldos(null, PageRequest.of(0, 10));
        assertThat(saldos.getContent()).hasSize(1);
        assertThat(saldos.getContent().get(0).getCardNumber()).isEqualTo("1234567890");
    }
}