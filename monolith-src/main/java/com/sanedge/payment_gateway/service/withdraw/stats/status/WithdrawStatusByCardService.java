package com.sanedge.payment_gateway.service.withdraw.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.MonthStatusWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearStatusWithdrawCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;

public interface WithdrawStatusByCardService {
    ApiResponse<List<WithdrawResponseMonthStatusSuccess>> findMonthStatusSuccessByCard(
            MonthStatusWithdrawCardNumber req);

    ApiResponse<List<WithdrawResponseYearStatusSuccess>> findYearlyStatusSuccessByCard(
            YearStatusWithdrawCardNumber req);

    ApiResponse<List<WithdrawResponseMonthStatusFailed>> findMonthStatusFailedByCard(MonthStatusWithdrawCardNumber req);

    ApiResponse<List<WithdrawResponseYearStatusFailed>> findYearlyStatusFailedByCard(YearStatusWithdrawCardNumber req);
}