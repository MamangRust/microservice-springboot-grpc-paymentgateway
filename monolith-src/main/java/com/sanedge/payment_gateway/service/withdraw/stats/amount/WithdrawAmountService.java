package com.sanedge.payment_gateway.service.withdraw.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;

public interface WithdrawAmountService {

    ApiResponse<List<WithdrawMonthlyAmountResponse>> findMonthlyWithdraws(Long year);

    ApiResponse<List<WithdrawYearlyAmountResponse>> findYearlyWithdraws(Long year);
}
