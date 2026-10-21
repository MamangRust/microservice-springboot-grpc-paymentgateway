package com.sanedge.payment_gateway.domain.responses.topup.stats.amount;

import com.sanedge.payment_gateway.models.topup.TopupMonthAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupMonthAmountResponse {
    private String monthName;
    private Long totalAmount;

    public static TopupMonthAmountResponse from(TopupMonthAmount dto) {
        return TopupMonthAmountResponse.builder()
                .monthName(dto.getMonthName())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}