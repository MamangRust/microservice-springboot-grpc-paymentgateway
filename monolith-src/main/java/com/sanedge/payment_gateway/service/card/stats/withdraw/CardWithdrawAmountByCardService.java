package com.sanedge.payment_gateway.service.card.stats.withdraw;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;

public interface CardWithdrawAmountByCardService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountByCard(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountByCard(MonthYearCardNumberCard req);
}
