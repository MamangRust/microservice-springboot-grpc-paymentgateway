package com.sanedge.payment_gateway.service.merchant;

import com.sanedge.payment_gateway.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.payment_gateway.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantResponseDeleteAt;

public interface MerchantCommandService {
    ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req);

    ApiResponse<MerchantResponseDeleteAt> trashMerchant(Long id);

    ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Long id);

    ApiResponse<Boolean> deleteMerchant(Long id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
