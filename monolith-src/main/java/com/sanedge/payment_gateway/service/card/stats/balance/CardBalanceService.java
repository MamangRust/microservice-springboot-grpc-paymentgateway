package com.sanedge.payment_gateway.service.card.stats.balance;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseYearBalance;

public interface CardBalanceService {
    ApiResponse<List<CardResponseMonthBalance>> findMonthBalance(Long year);

    ApiResponse<List<CardResponseYearBalance>> findYearBalance(Long year);
}
