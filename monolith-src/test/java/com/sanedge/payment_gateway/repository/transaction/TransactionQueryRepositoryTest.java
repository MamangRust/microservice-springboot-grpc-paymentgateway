package com.sanedge.payment_gateway.repository.transaction;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transaction.Transaction;

public class TransactionQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransactionQueryRepository queryRepository;

    @Autowired
    private TransactionCommandRepository commandRepository;

    private Long transactionId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Transaction transaction = new Transaction();
        transaction.setTransactionNo(UUID.randomUUID());
        transaction.setCardNumber(cardNumber);
        transaction.setAmount(100000L);
        transaction.setPaymentMethod("CREDIT_CARD");
        transaction.setMerchantId(1L);
        transaction.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        transaction.setStatus(Status.PENDING);

        Transaction saved = commandRepository.save(transaction);
        transactionId = saved.getTransactionId();
    }

    @Test
    void testFindTransactions() {
        Page<Transaction> page = queryRepository.findTransactions("1234", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getCardNumber()).contains("1234");
    }

    @Test
    void testFindActiveTransactions() {
        Page<Transaction> page = queryRepository.findActiveTransactions(null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getDeletedAt()).isNull();
    }

    @Test
    void testFindTrashedTransactions() {
        commandRepository.trashed(transactionId);
        Page<Transaction> page = queryRepository.findTrashedTransactions(null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getDeletedAt()).isNotNull();
    }

    @Test
    void testFindTransactionById() {
        Transaction transaction = queryRepository.findTransactionById(transactionId);
        assertThat(transaction).isNotNull();
        assertThat(transaction.getTransactionId()).isEqualTo(transactionId);
    }

    @Test
    void testFindTransactionsByCardNumber() {
        Page<Transaction> page = queryRepository.findTransactionsByCardNumber(cardNumber, null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getCardNumber()).isEqualTo(cardNumber);
    }

    @Test
    void testFindTransactionsByMerchantId() {
        List<Transaction> list = queryRepository.findTransactionsByMerchantId(1L);
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getMerchantId()).isEqualTo(1L);
    }
}