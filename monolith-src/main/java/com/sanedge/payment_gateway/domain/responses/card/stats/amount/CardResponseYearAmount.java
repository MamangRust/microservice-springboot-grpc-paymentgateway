package com.sanedge.payment_gateway.domain.responses.card.stats.amount;

import com.sanedge.payment_gateway.models.card.CardYearAmount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseYearAmount {
    private String reportYear;
    private Long totalAmount;

    public static CardResponseYearAmount from(CardYearAmount model) {
        return CardResponseYearAmount.builder()
                .reportYear(model.getReportYear())
                .totalAmount(model.getTotalAmount())
                .build();
    }
}