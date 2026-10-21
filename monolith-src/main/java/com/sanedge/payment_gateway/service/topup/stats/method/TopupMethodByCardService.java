package com.sanedge.payment_gateway.service.topup.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.topup.stats.YearMonthMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupYearlyMethodResponse;

public interface TopupMethodByCardService {
    ApiResponse<List<TopupMonthMethodResponse>> findMonthlyMethods(YearMonthMethod req);

    ApiResponse<List<TopupYearlyMethodResponse>> findYearlyMethods(YearMonthMethod req);
}
