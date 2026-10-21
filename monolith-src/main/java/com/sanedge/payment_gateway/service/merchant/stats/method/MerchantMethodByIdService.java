package com.sanedge.payment_gateway.service.merchant.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearPaymentMethodMerchant;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;

public interface MerchantMethodByIdService {
    ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethodById(MonthYearPaymentMethodMerchant req);

    ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethodById(MonthYearPaymentMethodMerchant req);
}
