package com.sanedge.payment_gateway.models.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMonthAmount {
    private String monthName;
    private Long totalAmount;
}
