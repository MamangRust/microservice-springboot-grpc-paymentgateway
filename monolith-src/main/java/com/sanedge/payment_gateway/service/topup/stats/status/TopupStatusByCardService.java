package com.sanedge.payment_gateway.service.topup.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.MonthTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.requests.topup.statsbycard.YearTopupStatusCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.topup.stats.status.TopupResponseYearStatusSuccess;

public interface TopupStatusByCardService {
    ApiResponse<List<TopupResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthTopupStatusCardNumber req);

    ApiResponse<List<TopupResponseYearStatusSuccess>> findYearlyStatusSuccess(YearTopupStatusCardNumber req);

    ApiResponse<List<TopupResponseMonthStatusFailed>> findMonthStatusFailed(MonthTopupStatusCardNumber req);

    ApiResponse<List<TopupResponseYearStatusFailed>> findYearlyStatusFailed(YearTopupStatusCardNumber req);
}
