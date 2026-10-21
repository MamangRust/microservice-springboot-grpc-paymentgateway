package com.sanedge.payment_gateway.domain.responses.merchant.stats.method;

import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyPaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResponseMonthlyPaymentMethod {
    private String monthName;
    private String paymentMethod;
    private Long totalAmount;

    public static MerchantResponseMonthlyPaymentMethod from(MerchantMonthlyPaymentMethod model) {
        return MerchantResponseMonthlyPaymentMethod.builder()
                .monthName(model.getMonthName())
                .paymentMethod(model.getPaymentMethod())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}