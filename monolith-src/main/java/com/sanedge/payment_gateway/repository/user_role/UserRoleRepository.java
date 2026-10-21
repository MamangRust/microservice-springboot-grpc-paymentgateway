package com.sanedge.payment_gateway.repository.user_role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.payment_gateway.models.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
