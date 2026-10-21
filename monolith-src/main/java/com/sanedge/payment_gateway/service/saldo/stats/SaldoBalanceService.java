package com.sanedge.payment_gateway.service.saldo.stats;

import java.util.List;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoMonthBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.balance.SaldoYearBalanceResponse;

public interface SaldoBalanceService {
    ApiResponse<List<SaldoMonthBalanceResponse>> getMonthBalance(Long year);

    ApiResponse<List<SaldoYearBalanceResponse>> getYearBalance(Long year);
}
