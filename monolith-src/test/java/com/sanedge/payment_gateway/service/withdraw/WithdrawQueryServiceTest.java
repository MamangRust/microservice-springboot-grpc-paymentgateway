package com.sanedge.payment_gateway.service.withdraw;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdraws;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;

public class WithdrawQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private WithdrawQueryService queryService;

    @Autowired
    private WithdrawCommandRepository commandRepository;

    private Long withdrawId;
    private String cardNumber = "1234567890123456";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Withdraw withdraw = new Withdraw();
        withdraw.setWithdrawNo(UUID.randomUUID());
        withdraw.setCardNumber(cardNumber);
        withdraw.setWithdrawAmount(500000L);
        withdraw.setWithdrawTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

        Withdraw saved = commandRepository.save(withdraw);
        withdrawId = saved.getWithdrawId();
    }

    @Test
    void testFindAll() {
        FindAllWithdraws req = new FindAllWithdraws();
        req.setPage(1);
        req.setPageSize(10);

        ApiResponsePagination<List<WithdrawResponse>> response = queryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindAllByCardNumber() {
        FindAllWithdrawCardNumber req = new FindAllWithdrawCardNumber();
        req.setCardNumber(cardNumber);
        req.setPage(1);
        req.setPageSize(10);

        ApiResponsePagination<List<WithdrawResponse>> response = queryService.findAllByCardNumber(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindById() {
        ApiResponse<WithdrawResponse> response = queryService.findById(withdrawId);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData().getId()).isEqualTo(withdrawId);
    }

    @Test
    void testFindByCard() {
        ApiResponse<List<WithdrawResponse>> response = queryService.findByCard(cardNumber);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByActive() {
        FindAllWithdraws req = new FindAllWithdraws();
        ApiResponsePagination<List<WithdrawResponseDeleteAt>> response = queryService.findByActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void testFindByTrashed() {
        commandRepository.trashed(withdrawId);
        FindAllWithdraws req = new FindAllWithdraws();
        ApiResponsePagination<List<WithdrawResponseDeleteAt>> response = queryService.findByTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }
}