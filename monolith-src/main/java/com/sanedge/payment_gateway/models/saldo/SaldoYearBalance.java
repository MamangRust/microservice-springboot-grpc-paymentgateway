package com.sanedge.payment_gateway.models.saldo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoYearBalance {
    private String reportYear;
    private Long totalBalance;
}
