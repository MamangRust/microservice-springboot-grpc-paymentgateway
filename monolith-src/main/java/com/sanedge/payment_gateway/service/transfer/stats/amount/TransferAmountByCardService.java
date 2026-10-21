package com.sanedge.payment_gateway.service.transfer.stats.amount;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.MonthYearCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.amount.TransferYearAmountResponse;

public interface TransferAmountByCardService {
    ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmountsBySender(MonthYearCardNumber req);

    ApiResponse<List<TransferMonthAmountResponse>> findMonthlyAmountsByReceiver(MonthYearCardNumber req);

    ApiResponse<List<TransferYearAmountResponse>> findYearlyAmountsBySender(MonthYearCardNumber req);

    ApiResponse<List<TransferYearAmountResponse>> findYearlyAmountsByReceiver(MonthYearCardNumber req);
}
