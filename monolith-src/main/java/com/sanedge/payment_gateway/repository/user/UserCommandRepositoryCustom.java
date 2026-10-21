package com.sanedge.payment_gateway.repository.user;

import com.sanedge.payment_gateway.models.User;

public interface UserCommandRepositoryCustom {
    User trashed(Long userId);

    User restore(Long userId);

    User deletePermanent(Long userId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
