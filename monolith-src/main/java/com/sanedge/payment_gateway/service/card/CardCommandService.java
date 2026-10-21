package com.sanedge.payment_gateway.service.card;

import com.sanedge.payment_gateway.domain.requests.card.CreateCardRequest;
import com.sanedge.payment_gateway.domain.requests.card.UpdateCardRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponseDeleteAt;

public interface CardCommandService {
    ApiResponse<CardResponse> createCard(CreateCardRequest req);

    ApiResponse<CardResponse> updateCard(UpdateCardRequest req);

    ApiResponse<CardResponseDeleteAt> trashCard(Long id);

    ApiResponse<CardResponseDeleteAt> restoreCard(Long id);

    ApiResponse<Boolean> deleteCard(Long id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
