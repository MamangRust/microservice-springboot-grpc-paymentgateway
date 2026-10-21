package com.sanedge.payment_gateway.repository.withdraw;

import com.sanedge.payment_gateway.models.withdraw.Withdraw;

public interface WithdrawCommandRepositoryCustom {
    Withdraw updateStatus(Long withdrawId, String status);

    Withdraw trashed(Long withdrawId);

    Withdraw restore(Long withdrawId);

    boolean deletePermanent(Long withdrawId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
