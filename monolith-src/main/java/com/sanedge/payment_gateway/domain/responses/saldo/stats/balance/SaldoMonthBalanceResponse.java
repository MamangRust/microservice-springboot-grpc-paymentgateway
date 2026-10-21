package com.sanedge.payment_gateway.domain.responses.saldo.stats.balance;

import com.sanedge.payment_gateway.models.saldo.SaldoMonthBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaldoMonthBalanceResponse {
    private String monthName;
    private Long totalBalance;

    public static SaldoMonthBalanceResponse from(SaldoMonthBalance entity) {
        return SaldoMonthBalanceResponse.builder()
                .monthName(entity.getMonthName())
                .totalBalance(entity.getTotalBalance())
                .build();
    }
}