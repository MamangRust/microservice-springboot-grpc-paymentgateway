package com.sanedge.payment_gateway.service.transfer;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transfers.FindAllTransfers;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;

public interface TransferQueryService {

    ApiResponsePagination<List<TransferResponse>> findAll(FindAllTransfers req);

    ApiResponse<TransferResponse> findById(Long transferId);

    ApiResponsePagination<List<TransferResponseDeleteAt>> findByActive(FindAllTransfers req);

    ApiResponsePagination<List<TransferResponseDeleteAt>> findByTrashed(FindAllTransfers req);

    ApiResponse<List<TransferResponse>> findByTransferFrom(String transferFrom);

    ApiResponse<List<TransferResponse>> findByTransferTo(String transferTo);
}