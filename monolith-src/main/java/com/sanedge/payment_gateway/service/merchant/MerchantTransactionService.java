package com.sanedge.payment_gateway.service.merchant;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.merchant.FindAllMerchants;
import com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsByApiKey;
import com.sanedge.payment_gateway.domain.requests.merchant.transactions.FindAllMerchantTransactionsById;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.merchant.MerchantTransactionResponse;

public interface MerchantTransactionService {
    ApiResponsePagination<List<MerchantTransactionResponse>> findAll(FindAllMerchants req);

    ApiResponsePagination<List<MerchantTransactionResponse>> findById(FindAllMerchantTransactionsById req);

    ApiResponsePagination<List<MerchantTransactionResponse>> findByApiKey(FindAllMerchantTransactionsByApiKey req);
}
