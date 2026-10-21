package com.sanedge.payment_gateway.service.transaction;

import com.sanedge.payment_gateway.domain.requests.transaction.CreateTransactionRequest;
import com.sanedge.payment_gateway.domain.requests.transaction.UpdateTransactionRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;

public interface TransactionCommandService {

    ApiResponse<TransactionResponse> create(String apiKey, CreateTransactionRequest req);

    ApiResponse<TransactionResponse> update(String apiKey, UpdateTransactionRequest req);

    ApiResponse<TransactionResponseDeleteAt> trashed(Long transactionId);

    ApiResponse<TransactionResponseDeleteAt> restore(Long transactionId);

    ApiResponse<Boolean> deletePermanent(Long transactionId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}