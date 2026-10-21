package com.sanedge.payment_gateway.service.transfer;

import com.sanedge.payment_gateway.domain.requests.transfers.CreateTransferRequest;
import com.sanedge.payment_gateway.domain.requests.transfers.UpdateTransferRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;

public interface TransferCommandService {

    ApiResponse<TransferResponse> create(CreateTransferRequest req);

    ApiResponse<TransferResponse> update(UpdateTransferRequest req);

    ApiResponse<TransferResponseDeleteAt> trashed(Long transferId);

    ApiResponse<TransferResponseDeleteAt> restore(Long transferId);

    ApiResponse<Boolean> deletePermanent(Long transferId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}