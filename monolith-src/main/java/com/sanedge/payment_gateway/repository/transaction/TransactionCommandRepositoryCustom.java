package com.sanedge.payment_gateway.repository.transaction;

import com.sanedge.payment_gateway.models.transaction.Transaction;

public interface TransactionCommandRepositoryCustom {
    Transaction updateTransactionStatus(Long transactionId, String status);

    Transaction trashed(Long transactionId);

    Transaction restore(Long transactionId);

    boolean deletePermanent(Long transactionId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
