package com.sanedge.payment_gateway.models.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthMethod {
    private String monthName;
    private String paymentMethod;
    private Long totalTransactions;
    private Long totalAmount;
}
