package com.sanedge.payment_gateway.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.payment_gateway.models.Role;

@Repository
public interface RoleCommandRepository extends JpaRepository<Role, Long>, RoleCommandRepositoryCustom {
}
