package com.sanedge.payment_gateway.service.transaction.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionYearMethodResponse;

public interface TransactionMethodService {
    ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(Long year);

    ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(Long year);
}
