package com.sanedge.payment_gateway.service.transfer.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferYearAmountResponse;

public interface TransferAmountService {
    ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmounts(Long year);

    ApiResponse<List<TransferYearAmountResponse>> findYearlyAmounts(Long year);
}
