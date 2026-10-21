package com.sanedge.payment_gateway.repository.role;

import com.sanedge.payment_gateway.models.Role;

public interface RoleCommandRepositoryCustom {
    Role trashed(Long roleId);

    Role restore(Long roleId);

    Role deletePermanent(Long roleId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
