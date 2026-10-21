package com.sanedge.payment_gateway.repository.transaction;

import com.sanedge.payment_gateway.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCommandRepository
    extends JpaRepository<Transaction, Long>, TransactionCommandRepositoryCustom {

}
