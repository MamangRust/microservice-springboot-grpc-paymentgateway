package com.sanedge.payment_gateway.repository.withdraw;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;

public class WithdrawCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private Long withdrawId;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // Use adminCard from BaseIntegrationTest
        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber(adminCard.getCardNumber());
        withdraw.setWithdrawAmount(500000L);
        withdraw.setWithdrawTime(new Timestamp(System.currentTimeMillis()));
        withdraw.setStatus(Status.PENDING);

        Withdraw saved = commandRepository.save(withdraw);
        withdrawId = saved.getWithdrawId();
        entityManager.flush();
    }

    @Test
    void testUpdateWithdrawStatus() {
        Withdraw updated = commandRepository.updateStatus(withdrawId, "SUCCESS");
        assertThat(updated.getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void testTrashed() {
        Withdraw trashed = commandRepository.trashed(withdrawId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void testRestore() {
        commandRepository.trashed(withdrawId);
        Withdraw restored = commandRepository.restore(withdrawId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void testDeletePermanent() {
        commandRepository.trashed(withdrawId);
        boolean deleted = commandRepository.deletePermanent(withdrawId);
        assertThat(deleted).isTrue();
        assertThat(commandRepository.findById(withdrawId)).isEmpty();
    }

    @Test
    void testRestoreAllDeleted() {
        commandRepository.trashed(withdrawId);
        boolean restored = commandRepository.restoreAllDeleted();
        assertThat(restored).isTrue();

        Withdraw withdraw = commandRepository.findById(withdrawId).orElseThrow();
        assertThat(withdraw.getDeletedAt()).isNull();
    }

    @Test
    void testDeleteAllDeleted() {
        commandRepository.trashed(withdrawId);
        boolean deleted = commandRepository.deleteAllDeleted();
        assertThat(deleted).isTrue();
        assertThat(commandRepository.findById(withdrawId)).isEmpty();
    }
}