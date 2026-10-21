package com.sanedge.payment_gateway.service.withdraw.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearMonthCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;

public interface WithdrawAmountByCardService {

    ApiResponse<List<WithdrawMonthlyAmountResponse>> findMonthlyByCardNumber(YearMonthCardNumber req);

    ApiResponse<List<WithdrawYearlyAmountResponse>> findYearlyByCardNumber(YearMonthCardNumber req);
}
