package com.sanedge.payment_gateway.domain.responses.topup.stats.status;

import com.sanedge.payment_gateway.models.topup.TopupYearStatusSuccess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponseYearStatusSuccess {
    private String reportYear;
    private Long totalAmount;
    private Long totalSuccess;

    public static TopupResponseYearStatusSuccess from(TopupYearStatusSuccess dto) {
        return TopupResponseYearStatusSuccess.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalSuccess(dto.getTotalSuccess())
                .build();
    }
}
