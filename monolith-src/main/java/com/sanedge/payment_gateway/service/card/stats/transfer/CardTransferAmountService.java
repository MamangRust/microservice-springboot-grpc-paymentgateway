package com.sanedge.payment_gateway.service.card.stats.transfer;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;

public interface CardTransferAmountService {
    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(Long year);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(Long year);

    ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(Long year);

    ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(Long year);
}
