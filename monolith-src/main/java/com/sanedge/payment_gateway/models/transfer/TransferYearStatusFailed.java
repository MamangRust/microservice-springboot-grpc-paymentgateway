package com.sanedge.payment_gateway.models.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferYearStatusFailed {
    private String reportYear;
    private Long totalFailed;
    private Long totalAmount;
}
