package com.sanedge.payment_gateway.domain.responses.topup.stats.method;

import com.sanedge.payment_gateway.models.topup.TopupMonthMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupMonthMethodResponse {
    private String monthName;
    private String topupMethod;
    private Long totalTopups;
    private Long totalAmount;

    public static TopupMonthMethodResponse from(TopupMonthMethod dto) {
        return TopupMonthMethodResponse.builder()
                .monthName(dto.getMonthName())
                .topupMethod(dto.getTopupMethod())
                .totalTopups(dto.getTotalTopups())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}