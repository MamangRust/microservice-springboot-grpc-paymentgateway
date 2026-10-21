package com.sanedge.payment_gateway.service.merchant.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearPaymentMethodApiKey;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;

public interface MerchantMethodByApiKeyService {
    ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethod(MonthYearPaymentMethodApiKey req);

    ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethod(MonthYearPaymentMethodApiKey req);
}
