package com.sanedge.payment_gateway.service.withdraw;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.FindAllWithdraws;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;

public interface WithdrawQueryService {

    ApiResponsePagination<List<WithdrawResponse>> findAll(FindAllWithdraws req);

    ApiResponsePagination<List<WithdrawResponse>> findAllByCardNumber(FindAllWithdrawCardNumber req);

    ApiResponse<WithdrawResponse> findById(Long withdrawId);

    ApiResponse<List<WithdrawResponse>> findByCard(String cardNumber);

    ApiResponsePagination<List<WithdrawResponseDeleteAt>> findByActive(FindAllWithdraws req);

    ApiResponsePagination<List<WithdrawResponseDeleteAt>> findByTrashed(FindAllWithdraws req);
}
