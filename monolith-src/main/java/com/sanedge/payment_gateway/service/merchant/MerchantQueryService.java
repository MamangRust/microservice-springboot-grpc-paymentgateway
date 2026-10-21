package com.sanedge.payment_gateway.service.merchant;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponseDeleteAt;

public interface MerchantQueryService {
    ApiResponsePagination<List<MerchantResponse>> findAll(FindAllMerchants req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByActive(FindAllMerchants req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByTrashed(FindAllMerchants req);

    ApiResponse<MerchantResponse> findById(Long cardId);

    ApiResponse<MerchantResponse> findByApiKey(String api_key);

    ApiResponse<List<MerchantResponse>> findByUserId(Long user_id);
}
