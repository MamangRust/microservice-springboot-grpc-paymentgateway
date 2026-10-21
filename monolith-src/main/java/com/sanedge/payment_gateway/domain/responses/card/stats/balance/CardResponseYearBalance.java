package com.sanedge.payment_gateway.domain.responses.card.stats.balance;

import com.sanedge.payment_gateway.models.card.CardYearBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseYearBalance {
    private String reportYear;
    private Long totalBalance;

    public static CardResponseYearBalance from(CardYearBalance model) {
        return CardResponseYearBalance.builder()
                .reportYear(model.getReportYear())
                .totalBalance(model.getTotalBalance())
                .build();
    }
}
