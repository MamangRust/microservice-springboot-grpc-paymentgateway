package com.sanedge.payment_gateway.repository.merchants;

import com.sanedge.payment_gateway.models.merchants.Merchant;

public interface MerchantCommandRepositoryCustom {
    Merchant trashed(Long merchantId);

    Merchant restore(Long merchantId);

    boolean deletePermanent(Long merchantId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();

    boolean updateStatus(Long merchantId, String status);
}
