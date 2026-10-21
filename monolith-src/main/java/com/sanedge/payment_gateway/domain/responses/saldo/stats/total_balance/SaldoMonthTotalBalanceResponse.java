package com.sanedge.payment_gateway.domain.responses.saldo.stats.total_balance;

import com.sanedge.payment_gateway.models.saldo.SaldoMonthTotalBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoMonthTotalBalanceResponse {
    private String monthName;
    private String reportYear;
    private Long totalBalance;

    public static SaldoMonthTotalBalanceResponse from(SaldoMonthTotalBalance entity) {
        return SaldoMonthTotalBalanceResponse.builder()
                .monthName(entity.getMonthName())
                .reportYear(entity.getReportYear())
                .totalBalance(entity.getTotalBalance())
                .build();
    }
}