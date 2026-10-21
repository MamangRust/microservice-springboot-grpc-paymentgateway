package com.sanedge.payment_gateway.service.merchant.stats.method;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearPaymentMethodMerchant;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.merchants.MerchantCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;

public class MerchantMethodByIdServiceTest extends BaseIntegrationTest {

    @Autowired
    private MerchantMethodByIdService merchantMethodByIdService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private TransactionCommandRepository transactionCommandRepository;

    @BeforeEach
    void setUp() {
        transactionCommandRepository.deleteAll();
        merchantCommandRepository.deleteAll();

        Merchant merchant = new Merchant();
        merchant.setName("Test Merchant");
        merchant.setApiKey("test-api-key");
        merchant.setMerchantNo(UUID.randomUUID());
        merchant.setMerchantId(null);
        merchant.setUserId(1L);
        merchant = merchantCommandRepository.save(merchant);

        Transaction tx = new Transaction();
        tx.setTransactionNo(UUID.randomUUID());
        tx.setCardNumber("1111222233334444");
        tx.setMerchantId(merchant.getMerchantId());
        tx.setAmount(100000L);
        tx.setPaymentMethod("CARD");
        tx.setTransactionTime(new Timestamp(System.currentTimeMillis()));
        tx.setStatus(Status.SUCCESS);
        transactionCommandRepository.save(tx);
    }

    @Test
    void shouldFindMonthMethodById() {
        Merchant merchant = merchantCommandRepository.findAll().get(0);
        MonthYearPaymentMethodMerchant req = new MonthYearPaymentMethodMerchant();
        req.setMerchantId(merchant.getMerchantId());
        req.setYear((long) LocalDate.now().getYear());

        ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> response = merchantMethodByIdService.findMonthMethodById(req);
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void shouldFindYearMethodById() {
        Merchant merchant = merchantCommandRepository.findAll().get(0);
        MonthYearPaymentMethodMerchant req = new MonthYearPaymentMethodMerchant();
        req.setMerchantId(merchant.getMerchantId());
        req.setYear((long) LocalDate.now().getYear());

        ApiResponse<List<com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod>> response = merchantMethodByIdService.findYearMethodById(req);
        assertThat(response.getData()).isNotNull();
    }
}