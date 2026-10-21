package com.sanedge.payment_gateway.domain.responses.topup.stats.amount;

import com.sanedge.payment_gateway.models.topup.TopupYearAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupYearlyAmountResponse {
    private String reportYear;
    private Long totalAmount;

    public static TopupYearlyAmountResponse from(TopupYearAmount dto) {
        return TopupYearlyAmountResponse.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}