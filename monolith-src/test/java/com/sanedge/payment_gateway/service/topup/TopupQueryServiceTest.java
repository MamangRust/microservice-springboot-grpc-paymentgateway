package com.sanedge.payment_gateway.service.topup;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopups;
import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopupsByCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;

public class TopupQueryServiceTest extends BaseIntegrationTest {

    @Autowired
    private TopupQueryService topupQueryService;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        topupCommandRepository.deleteAll();

        Topup topup = new Topup();
        topup.setCardNumber("1234567890");
        topup.setTopupAmount(100000L);
        topup.setTopupMethod("VA_BCA");
        topup.setTopupNo(java.util.UUID.randomUUID());
        topup.setTopupTime(new java.sql.Timestamp(System.currentTimeMillis()));
        topupCommandRepository.save(topup);
    }

    @Test
    void shouldFindAll() {
        FindAllTopups req = new FindAllTopups();
        ApiResponsePagination<List<TopupResponse>> response = topupQueryService.findAll(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindAllByCardNumber() {
        FindAllTopupsByCardNumber req = new FindAllTopupsByCardNumber();
        req.setCardNumber("1234567890");
        ApiResponsePagination<List<TopupResponse>> response = topupQueryService.findAllByCardNumber(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindActive() {
        FindAllTopups req = new FindAllTopups();
        ApiResponsePagination<List<TopupResponseDeleteAt>> response = topupQueryService.findActive(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindTrashed() {
        FindAllTopups req = new FindAllTopups();
        ApiResponsePagination<List<TopupResponseDeleteAt>> response = topupQueryService.findTrashed(req);
        assertThat(response.getStatus()).isEqualTo("success");
    }

    @Test
    void shouldFindByCard() {
        ApiResponse<List<TopupResponse>> response = topupQueryService.findByCard("1234567890");
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getData()).isNotEmpty();
    }

    @Test
    void shouldFindById() {
        Topup topup = topupCommandRepository.findAll().get(0);
        ApiResponse<TopupResponse> response = topupQueryService.findById(topup.getTopupId());
        assertThat(response.getStatus()).isEqualTo("success");
    }
}