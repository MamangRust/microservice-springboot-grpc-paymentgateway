package com.sanedge.payment_gateway.service.merchant.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseYearlyAmount;

public interface MerchantAmountService {
    ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmount(Long year);

    ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmount(Long year);
}
