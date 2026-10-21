package com.sanedge.payment_gateway.domain.responses.merchant.stats.method;

import com.sanedge.payment_gateway.models.merchants.MerchantYearlyPaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseYearlyPaymentMethod {
    private String reportYear;
    private String paymentMethod;
    private Long totalAmount;

    public static MerchantResponseYearlyPaymentMethod from(MerchantYearlyPaymentMethod model) {
        return MerchantResponseYearlyPaymentMethod.builder()
                .reportYear(model.getReportYear())
                .paymentMethod(model.getPaymentMethod())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}
