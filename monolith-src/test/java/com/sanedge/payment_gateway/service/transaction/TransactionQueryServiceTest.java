package com.sanedge.payment_gateway.service.transaction;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactions;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class TransactionQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionQueryService queryService;

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
        transaction.setTransactionTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

        Transaction saved = commandRepository.save(transaction);
        transactionId = saved.getTransactionId();
    }

    @Test
    void testFindAll() {
        FindAllTransactions req = new FindAllTransactions();
        req.setPage(1);
        req.setPageSize(10);

        ApiResponsePagination<List<TransactionResponse>> response = queryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindAllByCardNumber() {
        FindAllTransactionCardNumber req = new FindAllTransactionCardNumber();
        req.setCardNumber(cardNumber);
        req.setPage(1);
        req.setPageSize(10);

        ApiResponsePagination<List<TransactionResponse>> response = queryService.findAllByCardNumber(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByActive() {
        FindAllTransactions req = new FindAllTransactions();
        ApiResponsePagination<List<TransactionResponseDeleteAt>> response = queryService.findByActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByTrashed() {
        commandRepository.trashed(transactionId);
        FindAllTransactions req = new FindAllTransactions();
        ApiResponsePagination<List<TransactionResponseDeleteAt>> response = queryService.findByTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindById() {
        ApiResponse<TransactionResponse> response = queryService.findById(transactionId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getId()).isEqualTo(transactionId);
    }

    @Test
    void testFindByMerchantId() {
        ApiResponse<List<TransactionResponse>> response = queryService.findByMerchantId(1L);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }
}