package com.sanedge.payment_gateway.service.transaction.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transaction.statsbycard.MonthStatusTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.statsbycard.YearStatusTransactionCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.status.TransactionResponseYearStatusSuccess;

public interface TransactionStatusByCardService {
    ApiResponse<List<TransactionResponseMonthStatusSuccess>> findMonthStatusSuccess(
            MonthStatusTransactionCardNumber req);

    ApiResponse<List<TransactionResponseYearStatusSuccess>> findYearlyStatusSuccess(
            YearStatusTransactionCardNumber req);

    ApiResponse<List<TransactionResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransactionCardNumber req);

    ApiResponse<List<TransactionResponseYearStatusFailed>> findYearlyStatusFailed(YearStatusTransactionCardNumber req);
}
