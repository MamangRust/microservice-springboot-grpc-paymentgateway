package com.sanedge.payment_gateway.repository.merchants;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.payment_gateway.models.merchants.Merchant;

@Repository
public interface MerchantCommandRepository extends JpaRepository<Merchant, Long>, MerchantCommandRepositoryCustom {

}
