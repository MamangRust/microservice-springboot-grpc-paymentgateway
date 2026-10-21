package com.sanedge.payment_gateway.service.topup.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.topup.stats.MonthTopupStatus;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusSuccess;

public interface TopupStatusService {
    ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthTopupStatus req);

    ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatus req);

    ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
