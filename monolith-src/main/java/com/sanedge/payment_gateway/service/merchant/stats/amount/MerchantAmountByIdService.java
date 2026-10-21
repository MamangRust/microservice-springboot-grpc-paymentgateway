package com.sanedge.payment_gateway.service.merchant.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyid.MonthYearAmountMerchant;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseYearlyAmount;

public interface MerchantAmountByIdService {
    ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmountById(MonthYearAmountMerchant req);

    ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmountById(MonthYearAmountMerchant req);
}
