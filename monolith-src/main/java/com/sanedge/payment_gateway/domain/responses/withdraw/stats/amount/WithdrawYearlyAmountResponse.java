package com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount;

import com.sanedge.payment_gateway.models.withdraw.WithdrawYearlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawYearlyAmountResponse {
    private String reportYear;
    private Long totalAmount;

    public static WithdrawYearlyAmountResponse from(WithdrawYearlyAmount dto) {
        return WithdrawYearlyAmountResponse.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}