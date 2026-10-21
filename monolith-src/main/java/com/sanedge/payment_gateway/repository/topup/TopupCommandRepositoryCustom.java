package com.sanedge.payment_gateway.repository.topup;

import com.sanedge.payment_gateway.models.topup.Topup;

public interface TopupCommandRepositoryCustom {
    Topup updateTopupAmount(Long topupId, Long amount);

    Topup updateTopupStatus(Long topupId, String status);

    Topup trashed(Long topupId);

    Topup restore(Long topupId);

    boolean deletePermanent(Long topupId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
