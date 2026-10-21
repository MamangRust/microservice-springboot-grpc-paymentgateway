package com.sanedge.payment_gateway.service.topup;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopups;
import com.sanedge.payment_gateway.domain.requests.topup.FindAllTopupsByCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;

public interface TopupQueryService {

    ApiResponsePagination<List<TopupResponse>> findAll(FindAllTopups req);

    ApiResponsePagination<List<TopupResponse>> findAllByCardNumber(FindAllTopupsByCardNumber req);

    ApiResponsePagination<List<TopupResponseDeleteAt>> findActive(FindAllTopups req);

    ApiResponsePagination<List<TopupResponseDeleteAt>> findTrashed(FindAllTopups req);

    ApiResponse<List<TopupResponse>> findByCard(String cardNumber);

    ApiResponse<TopupResponse> findById(Long topupId);
}