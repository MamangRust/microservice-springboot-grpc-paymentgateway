package com.sanedge.payment_gateway.repository.withdraw;

import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawCommandRepository extends JpaRepository<Withdraw, Long>, WithdrawCommandRepositoryCustom {
}
