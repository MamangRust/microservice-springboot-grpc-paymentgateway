package com.sanedge.payment_gateway.service.card;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.card.FindAllCards;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponseDeleteAt;

public interface CardQueryService {
    ApiResponsePagination<List<CardResponse>> findAll(FindAllCards req);

    ApiResponsePagination<List<CardResponseDeleteAt>> findByActive(FindAllCards req);

    ApiResponsePagination<List<CardResponseDeleteAt>> findByTrashed(FindAllCards req);

    ApiResponse<CardResponse> findById(Long cardId);

    ApiResponse<CardResponse> findByUserId(Long userId);

    ApiResponse<CardResponse> findByCardNumber(String cardNumber);
}
