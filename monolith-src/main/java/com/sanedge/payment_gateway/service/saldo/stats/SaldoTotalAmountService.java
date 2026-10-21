package com.sanedge.payment_gateway.service.saldo.stats;

import java.util.List;

import com.sanedge.payment_gateway.domain.requests.saldo.MonthTotalSaldoBalance;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoMonthTotalBalanceResponse;
import com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance.SaldoYearTotalBalanceResponse;

public interface SaldoTotalAmountService {
    ApiResponse<List<SaldoMonthTotalBalanceResponse>> findMonthTotalBalance(MonthTotalSaldoBalance req);

    ApiResponse<List<SaldoYearTotalBalanceResponse>> findYearTotalBalance(Long year);
}
