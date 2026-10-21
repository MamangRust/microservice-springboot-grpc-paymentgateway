package com.sanedge.payment_gateway.service.transaction.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;

public interface TransactionAmountService {
    ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(Long year);

    ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(Long year);
}
