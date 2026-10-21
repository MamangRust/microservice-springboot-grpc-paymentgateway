package com.sanedge.payment_gateway.service.transaction.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthYearPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;

public interface TransactionAmountByCardService {
    ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(MonthYearPaymentMethod req);

    ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(MonthYearPaymentMethod req);
}
