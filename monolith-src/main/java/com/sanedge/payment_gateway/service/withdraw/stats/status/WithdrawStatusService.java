package com.sanedge.payment_gateway.service.withdraw.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.withdraws.MonthStatusWithdraw;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.status.WithdrawResponseYearStatusSuccess;

public interface WithdrawStatusService {
    ApiResponse<List<WithdrawResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusWithdraw req);

    ApiResponse<List<WithdrawResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<WithdrawResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusWithdraw req);

    ApiResponse<List<WithdrawResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
