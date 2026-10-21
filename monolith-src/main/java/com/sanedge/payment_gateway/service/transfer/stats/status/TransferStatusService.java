package com.sanedge.payment_gateway.service.transfer.stats.status;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transfers.stats.MonthStatusTransfer;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseMonthStatusSuccess;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusFailed;
import com.sanedge.payment_gateway.domain.responses.transfer.stats.status.TransferResponseYearStatusSuccess;

public interface TransferStatusService {
    ApiResponse<List<TransferResponseMonthStatusSuccess>> findMonthStatusSuccess(MonthStatusTransfer req);

    ApiResponse<List<TransferResponseYearStatusSuccess>> findYearlyStatusSuccess(Long year);

    ApiResponse<List<TransferResponseMonthStatusFailed>> findMonthStatusFailed(MonthStatusTransfer req);

    ApiResponse<List<TransferResponseYearStatusFailed>> findYearlyStatusFailed(Long year);
}
