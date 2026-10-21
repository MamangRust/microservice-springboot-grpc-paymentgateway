package com.sanedge.payment_gateway.repository.user;

import com.sanedge.payment_gateway.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommandRepository extends JpaRepository<User, Long>, UserCommandRepositoryCustom {

}
