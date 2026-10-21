package com.sanedge.payment_gateway.service.transaction.stats.method;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthYearPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionYearMethodResponse;

public interface TransactionMethodByCardService {
    ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(MonthYearPaymentMethod req);

    ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(MonthYearPaymentMethod req);
}
