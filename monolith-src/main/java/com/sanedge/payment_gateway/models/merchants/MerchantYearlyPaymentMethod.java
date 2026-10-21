package com.sanedge.payment_gateway.models.merchants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantYearlyPaymentMethod {
    private String reportYear;
    private String paymentMethod;
    private Long totalAmount;
}
