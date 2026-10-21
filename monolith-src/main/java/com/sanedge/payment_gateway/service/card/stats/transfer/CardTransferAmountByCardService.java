package com.sanedge.payment_gateway.service.card.stats.transfer;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;

public interface CardTransferAmountByCardService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(MonthYearCardNumberCard req);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(MonthYearCardNumberCard req);
}
