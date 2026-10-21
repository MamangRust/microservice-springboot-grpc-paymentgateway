package com.sanedge.payment_gateway.repository.saldo;

import com.sanedge.payment_gateway.models.saldo.Saldo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaldoCommandRepository extends JpaRepository<Saldo, Long>, SaldoCommandRepositoryCustom {

}