package com.sanedge.payment_gateway.repository.transfer;

import com.sanedge.payment_gateway.models.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferCommandRepository extends JpaRepository<Transfer, Long>, TransferCommandRepositoryCustom {

}
