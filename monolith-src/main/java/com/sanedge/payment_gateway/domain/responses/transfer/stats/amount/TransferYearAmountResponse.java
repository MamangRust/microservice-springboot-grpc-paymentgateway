package com.sanedge.payment_gateway.domain.responses.transfer.stats.amount;

import com.sanedge.payment_gateway.models.transfer.TransferYearAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferYearAmountResponse {
    private String reportYear;
    private Long totalAmount;

    public static TransferYearAmountResponse from(TransferYearAmount dto) {
        return TransferYearAmountResponse.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .build();
    }
}
