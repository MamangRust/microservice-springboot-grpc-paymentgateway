package com.sanedge.payment_gateway.service.merchant.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;

public interface MerchantMethodService {
    ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethod(Long year);

    ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethod(Long year);
}
