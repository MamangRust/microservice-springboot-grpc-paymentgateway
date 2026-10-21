package com.sanedge.payment_gateway.domain.responses.transaction.stats.status;

import com.sanedge.payment_gateway.models.transaction.TransactionYearStatusFailed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseYearStatusFailed {
    private String reportYear;
    private Long totalAmount;
    private Long totalFailed;

    public static TransactionResponseYearStatusFailed from(TransactionYearStatusFailed dto) {
        return TransactionResponseYearStatusFailed.builder()
                .reportYear(dto.getReportYear())
                .totalAmount(dto.getTotalAmount())
                .totalFailed(dto.getTotalFailed())
                .build();
    }
}