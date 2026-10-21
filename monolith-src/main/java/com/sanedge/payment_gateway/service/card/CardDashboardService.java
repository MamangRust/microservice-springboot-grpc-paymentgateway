package com.sanedge.payment_gateway.service.card;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboard;
import com.sanedge.payment_gateway.domain.responses.card.dashboard.CardDashboardCard;

public interface CardDashboardService {
    ApiResponse<CardDashboard> dashboard();

    ApiResponse<CardDashboardCard> dashboardByCard(String card_number);
}
