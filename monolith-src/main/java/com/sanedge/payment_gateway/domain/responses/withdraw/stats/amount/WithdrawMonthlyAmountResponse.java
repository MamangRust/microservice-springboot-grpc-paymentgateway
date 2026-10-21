package com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount;

import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawMonthlyAmountResponse {
    private String monthName;
    private Long totalAmount;

    public static WithdrawMonthlyAmountResponse from(WithdrawMonthlyAmount dto) {
        return WithdrawMonthlyAmountResponse.builder()
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}