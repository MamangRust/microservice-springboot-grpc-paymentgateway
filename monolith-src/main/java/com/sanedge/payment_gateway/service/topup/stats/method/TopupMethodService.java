package com.sanedge.payment_gateway.service.topup.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.method.TopupYearlyMethodResponse;

public interface TopupMethodService {
    ApiResponse<List<TopupMonthMethodResponse>> findMonthlyMethods(Long year);

    ApiResponse<List<TopupYearlyMethodResponse>> findYearlyMethods(Long year);
}
