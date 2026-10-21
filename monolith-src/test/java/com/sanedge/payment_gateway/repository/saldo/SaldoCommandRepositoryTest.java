package com.sanedge.payment_gateway.repository.saldo;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.models.saldo.Saldo;

public class SaldoCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SaldoCommandRepository saldoCommandRepository;

    private Long saldoId;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks

        Saldo saldo = new Saldo();
        saldo.setCardNumber("1234567890");
        saldo.setTotalBalance(100000L);
        Saldo saved = saldoCommandRepository.save(saldo);
        this.saldoId = saved.getSaldoId();
    }

    @Test
    void shouldSaveSaldo() {
        Saldo saldo = new Saldo();
        saldo.setCardNumber("0987654321");
        saldo.setTotalBalance(200000L);
        Saldo saved = saldoCommandRepository.save(saldo);
        assertThat(saved.getSaldoId()).isNotNull();
    }

    @Test
    void shouldUpdateSaldo() {
        Saldo saldo = saldoCommandRepository.findById(saldoId).orElseThrow();
        saldo.setTotalBalance(150000L);
        Saldo updated = saldoCommandRepository.save(saldo);
        assertThat(updated.getTotalBalance()).isEqualTo(150000L);
    }

    @Test
    void shouldTrashSaldo() {
        Saldo trashed = saldoCommandRepository.trashed(saldoId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldRestoreSaldo() {
        saldoCommandRepository.trashed(saldoId);
        Saldo restored = saldoCommandRepository.restore(saldoId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeletePermanent() {
        saldoCommandRepository.deletePermanent(saldoId);
        Optional<Saldo> saldo = saldoCommandRepository.findById(saldoId);
        assertThat(saldo).isEmpty();
    }

    @Test
    void shouldRestoreAllDeleted() {
        saldoCommandRepository.trashed(saldoId);
        boolean result = saldoCommandRepository.restoreAllDeleted();
        entityManager.clear();
        assertThat(result).isTrue();
        Saldo saldo = saldoCommandRepository.findById(saldoId).orElseThrow();
        assertThat(saldo.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeleteAllDeleted() {
        saldoCommandRepository.trashed(saldoId);
        boolean result = saldoCommandRepository.deleteAllDeleted();
        entityManager.clear();
        assertThat(result).isTrue();
        Optional<Saldo> saldo = saldoCommandRepository.findById(saldoId);
        assertThat(saldo).isEmpty();
    }
}