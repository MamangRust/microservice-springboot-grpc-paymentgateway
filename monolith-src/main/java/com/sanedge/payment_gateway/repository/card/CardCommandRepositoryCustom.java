package com.sanedge.payment_gateway.repository.card;

import com.sanedge.payment_gateway.models.card.Card;

public interface CardCommandRepositoryCustom {
    Card trashed(Long cardId);

    Card restore(Long cardId);

    boolean deletePermanent(Long cardId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}