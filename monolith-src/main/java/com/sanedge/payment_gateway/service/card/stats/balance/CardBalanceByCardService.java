package com.sanedge.payment_gateway.service.card.stats.balance;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseYearBalance;

public interface CardBalanceByCardService {
    ApiResponse<List<CardResponseMonthBalance>> findMonthBalanceByCard(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearBalance>> findYearBalanceByCard(MonthYearCardNumberCard req);
}
