package com.sanedge.payment_gateway.service.merchant.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearAmountApiKey;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseYearlyAmount;

public interface MerchantAmountByApiKeyService {
    ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmountByApiKey(MonthYearAmountApiKey req);

    ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmountByApiKey(MonthYearAmountApiKey req);
}
