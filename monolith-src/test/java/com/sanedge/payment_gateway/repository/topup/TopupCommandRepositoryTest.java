package com.sanedge.payment_gateway.repository.topup;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.topup.Topup;

public class TopupCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    private Long topupId;

    @BeforeEach
    void setUp() {
        topupCommandRepository.deleteAll();

        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber("1234567890");
        topup.setTopupAmount(100000L);
        topup.setTopupMethod("VA_BCA");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.PENDING);
        Topup saved = topupCommandRepository.save(topup);
        this.topupId = saved.getTopupId();
    }

    @Test
    void shouldSaveTopup() {
        Topup topup = new Topup();
        topup.setTopupNo(UUID.randomUUID());
        topup.setCardNumber("0987654321");
        topup.setTopupAmount(200000L);
        topup.setTopupMethod("VA_BNI");
        topup.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup.setStatus(Status.PENDING);
        Topup saved = topupCommandRepository.save(topup);
        assertThat(saved.getTopupId()).isNotNull();
    }

    @Test
    void shouldUpdateTopup() {
        Topup topup = topupCommandRepository.findById(topupId).orElseThrow();
        topup.setStatus(Status.SUCCESS);
        Topup updated = topupCommandRepository.save(topup);
        assertThat(updated.getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void shouldTrashTopup() {
        Topup trashed = topupCommandRepository.trashed(topupId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void shouldRestoreTopup() {
        topupCommandRepository.trashed(topupId);
        Topup restored = topupCommandRepository.restore(topupId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeletePermanent() {
        topupCommandRepository.deletePermanent(topupId);
        Optional<Topup> topup = topupCommandRepository.findById(topupId);
        assertThat(topup).isEmpty();
    }

    @Test
    void shouldRestoreAllDeleted() {
        topupCommandRepository.trashed(topupId);
        boolean result = topupCommandRepository.restoreAllDeleted();
        assertThat(result).isTrue();
        Topup topup = topupCommandRepository.findById(topupId).orElseThrow();
        assertThat(topup.getDeletedAt()).isNull();
    }

    @Test
    void shouldDeleteAllDeleted() {
        topupCommandRepository.trashed(topupId);
        boolean result = topupCommandRepository.deleteAllDeleted();
        assertThat(result).isTrue();
        Optional<Topup> topup = topupCommandRepository.findById(topupId);
        assertThat(topup).isEmpty();
    }
}