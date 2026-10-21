package com.sanedge.payment_gateway.repository.transfer;

import com.sanedge.payment_gateway.models.transfer.Transfer;

public interface TransferCommandRepositoryCustom {
    Transfer updateTransferAmount(Long transferId, Long amount);

    Transfer updateTransferStatus(Long transferId, String status);

    Transfer trashed(Long transferId);

    Transfer restore(Long transferId);

    boolean deletePermanent(Long transferId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
