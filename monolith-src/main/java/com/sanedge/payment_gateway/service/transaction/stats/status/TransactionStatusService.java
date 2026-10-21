package com.sanedge.payment_gateway.service.transaction.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthStatusTransaction;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusSuccess;

public interface TransactionStatusService {
    ApiResponse<List<TransactionResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusTransaction req);

    ApiResponse<List<TransactionResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<TransactionResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransaction req);

    ApiResponse<List<TransactionResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
