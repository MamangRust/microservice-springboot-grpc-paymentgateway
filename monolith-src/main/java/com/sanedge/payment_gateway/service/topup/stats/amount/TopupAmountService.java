package com.sanedge.payment_gateway.service.topup.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupYearlyAmountResponse;

public interface TopupAmountService {
    ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(Long year);

    ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(Long year);
}
