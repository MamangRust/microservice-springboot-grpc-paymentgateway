package com.sanedge.payment_gateway.service.topup.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.topup.stats.YearMonthMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupYearlyAmountResponse;

public interface TopupAmountByCardService {

    ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(YearMonthMethod req);

    ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(YearMonthMethod req);
}