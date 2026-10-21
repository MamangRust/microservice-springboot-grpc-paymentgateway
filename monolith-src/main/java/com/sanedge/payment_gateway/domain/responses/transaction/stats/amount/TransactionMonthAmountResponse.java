package com.sanedge.payment_gateway.domain.responses.transaction.stats.amount;

import com.sanedge.payment_gateway.models.transaction.TransactionMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthAmountResponse {
    private String monthName;
    private Long totalAmount;

    public static TransactionMonthAmountResponse from(TransactionMonthAmount dto) {
        return TransactionMonthAmountResponse.builder()
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}