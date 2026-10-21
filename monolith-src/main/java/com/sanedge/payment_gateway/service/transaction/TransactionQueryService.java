package com.sanedge.payment_gateway.service.transaction;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactionCardNumber;
import com.sanedge.payment_gateway.domain.requests.transaction.FindAllTransactions;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;

public interface TransactionQueryService {

    ApiResponsePagination<List<TransactionResponse>> findAll(FindAllTransactions req);

    ApiResponsePagination<List<TransactionResponse>> findAllByCardNumber(FindAllTransactionCardNumber req);

    ApiResponsePagination<List<TransactionResponseDeleteAt>> findByActive(FindAllTransactions req);

    ApiResponsePagination<List<TransactionResponseDeleteAt>> findByTrashed(FindAllTransactions req);

    ApiResponse<TransactionResponse> findById(Long transactionId);

    ApiResponse<List<TransactionResponse>> findByMerchantId(Long merchantId);
}