package com.sanedge.payment_gateway.repository.topup;

import com.sanedge.payment_gateway.models.topup.Topup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopupCommandRepository extends JpaRepository<Topup, Long>, TopupCommandRepositoryCustom {

}
