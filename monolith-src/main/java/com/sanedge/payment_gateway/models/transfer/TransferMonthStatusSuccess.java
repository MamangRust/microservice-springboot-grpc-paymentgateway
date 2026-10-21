package com.sanedge.payment_gateway.models.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferMonthStatusSuccess {
    private String reportYear;
    private String monthName;
    private Long totalSuccess;
    private Long totalAmount;
}
