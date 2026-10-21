package com.sanedge.payment_gateway.service.card.stats.topup;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;

public interface CardTopupAmountService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmount(Long year);

    ApiResponse<List<CardResponseYearAmount>> findYearAmount(Long year);
}
