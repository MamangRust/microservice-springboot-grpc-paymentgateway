package com.sanedge.payment_gateway.repository.transfer;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;

public class TransferCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferCommandRepository commandRepository;

    private Long transferId;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // No cleanup, relying on @Transactional rollbacks
        // Use seeded cards from BaseIntegrationTest
        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom(adminCard.getCardNumber());
        transfer.setTransferTo(regularCard.getCardNumber());
        transfer.setTransferAmount(500000L);
        transfer.setTransferTime(new Timestamp(System.currentTimeMillis()));
        transfer.setStatus(Status.PENDING);

        Transfer saved = commandRepository.save(transfer);
        transferId = saved.getTransferId();
        entityManager.flush();
    }

    @Test
    void testUpdateTransferStatus() {
        Transfer updated = commandRepository.updateTransferStatus(transferId, "SUCCESS");
        assertThat(updated.getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void testTrashed() {
        Transfer trashed = commandRepository.trashed(transferId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void testRestore() {
        commandRepository.trashed(transferId);
        Transfer restored = commandRepository.restore(transferId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void testDeletePermanent() {
        commandRepository.trashed(transferId);
        boolean deleted = commandRepository.deletePermanent(transferId);
        assertThat(deleted).isTrue();
        assertThat(commandRepository.findById(transferId)).isEmpty();
    }

    @Test
    void testRestoreAllDeleted() {
        commandRepository.trashed(transferId);
        boolean restored = commandRepository.restoreAllDeleted();
        entityManager.clear();
        assertThat(restored).isTrue();

        Transfer transfer = commandRepository.findById(transferId).orElseThrow();
        assertThat(transfer.getDeletedAt()).isNull();
    }

    @Test
    void testDeleteAllDeleted() {
        commandRepository.trashed(transferId);
        boolean deleted = commandRepository.deleteAllDeleted();
        entityManager.clear();
        assertThat(deleted).isTrue();
        assertThat(commandRepository.findById(transferId)).isEmpty();
    }
}