package com.sanedge.payment_gateway.service.merchant.stats.totalamount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearTotalAmountApiKey;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.total_amount.MerchantResponseMonthlyTotalAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.total_amount.MerchantResponseYearlyTotalAmount;

public interface MerchantTotalAmountByApiKeyService {
    ApiResponse<List<MerchantResponseMonthlyTotalAmount>> findMonthTotalAmountByApiKey(MonthYearTotalAmountApiKey req);

    ApiResponse<List<MerchantResponseYearlyTotalAmount>> findYearTotalAmountByApiKey(MonthYearTotalAmountApiKey req);
}
