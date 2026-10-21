package com.sanedge.payment_gateway.repository.card;

import com.sanedge.payment_gateway.models.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardCommandRepository extends JpaRepository<Card, Long>, CardCommandRepositoryCustom {

}
