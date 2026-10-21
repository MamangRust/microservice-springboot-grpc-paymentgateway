package com.sanedge.payment_gateway.models.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferMonthAmount {
    private String monthName;
    private Long totalAmount;
}
