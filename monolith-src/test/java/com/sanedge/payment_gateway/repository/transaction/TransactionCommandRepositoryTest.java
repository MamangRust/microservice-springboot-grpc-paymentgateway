package com.sanedge.payment_gateway.repository.transaction;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;

public class TransactionCommandRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionCommandRepository commandRepository;

    private Long transactionId;

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Transaction transaction = new Transaction();
        transaction.setTransactionNo(UUID.randomUUID());
        transaction.setCardNumber("1234567890123456");
        transaction.setAmount(100000L);
        transaction.setPaymentMethod("CREDIT_CARD");
        transaction.setMerchantId(1L);
        transaction.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        transaction.setStatus(Status.PENDING);

        Transaction saved = commandRepository.save(transaction);
        transactionId = saved.getTransactionId();
    }

    @Test
    void testUpdateTransactionStatus() {
        Transaction updated = commandRepository.updateTransactionStatus(transactionId, "SUCCESS");
        assertThat(updated.getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void testTrashed() {
        Transaction trashed = commandRepository.trashed(transactionId);
        assertThat(trashed.getDeletedAt()).isNotNull();
    }

    @Test
    void testRestore() {
        commandRepository.trashed(transactionId);
        Transaction restored = commandRepository.restore(transactionId);
        assertThat(restored.getDeletedAt()).isNull();
    }

    @Test
    void testDeletePermanent() {
        commandRepository.trashed(transactionId);
        boolean deleted = commandRepository.deletePermanent(transactionId);
        assertThat(deleted).isTrue();
        assertThat(commandRepository.findById(transactionId)).isEmpty();
    }

    @Test
    void testRestoreAllDeleted() {
        commandRepository.trashed(transactionId);
        boolean restored = commandRepository.restoreAllDeleted();
        assertThat(restored).isTrue();

        Transaction transaction = commandRepository.findById(transactionId).orElseThrow();
        assertThat(transaction.getDeletedAt()).isNull();
    }

    @Test
    void testDeleteAllDeleted() {
        commandRepository.trashed(transactionId);
        boolean deleted = commandRepository.deleteAllDeleted();
        assertThat(deleted).isTrue();
        assertThat(commandRepository.findById(transactionId)).isEmpty();
    }
}