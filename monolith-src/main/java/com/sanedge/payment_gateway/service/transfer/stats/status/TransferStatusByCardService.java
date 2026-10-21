package com.sanedge.payment_gateway.service.transfer.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.MonthStatusTransferCardNumber;
import com.sanedge.payment_gateway.domain.requests.transfers.statsbycard.YearStatusTransferCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusSuccess;

public interface TransferStatusByCardService {
    ApiResponse<List<TransferResponseMonthStatusSuccess>> findMonthStatusSuccessByCard(
            MonthStatusTransferCardNumber req);

    ApiResponse<List<TransferResponseYearStatusSuccess>> findYearlyStatusSuccessByCard(
            YearStatusTransferCardNumber req);

    ApiResponse<List<TransferResponseMonthStatusFailed>> findMonthStatusFailedByCard(MonthStatusTransferCardNumber req);

    ApiResponse<List<TransferResponseYearStatusFailed>> findYearlyStatusFailedByCard(YearStatusTransferCardNumber req);
}
