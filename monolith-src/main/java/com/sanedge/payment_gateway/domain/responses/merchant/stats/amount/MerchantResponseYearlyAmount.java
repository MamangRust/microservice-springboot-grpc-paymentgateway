package com.sanedge.payment_gateway.domain.responses.merchant.stats.amount;

import com.sanedge.payment_gateway.models.merchants.MerchantYearlyAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseYearlyAmount {
    private String reportYear;
    private Long totalAmount;

    public static MerchantResponseYearlyAmount from(MerchantYearlyAmount model) {
        return MerchantResponseYearlyAmount.builder()
                .reportYear(model.getReportYear())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}