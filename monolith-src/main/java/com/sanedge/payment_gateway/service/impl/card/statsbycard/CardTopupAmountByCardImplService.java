package com.sanedge.payment_gateway.service.impl.card.statsbycard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.payment_gateway.models.card.CardMonthAmount;
import com.sanedge.payment_gateway.models.card.CardYearAmount;
import com.sanedge.payment_gateway.repository.card.statsbycard.CardTopupAmountByCardRepository;
import com.sanedge.payment_gateway.service.card.stats.topup.CardTopupAmountByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTopupAmountByCardImplService implements CardTopupAmountByCardService {
        private final CardTopupAmountByCardRepository cardTopupAmountByCardRepository;

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountByCard(MonthYearCardNumberCard req) {
                log.info("📊 Fetching monthly top-up stats for card={} year={}", req.getCardNumber(), req.getYear());

                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardTopupAmountByCardRepository
                                        .getMonthlyTopupAmountByCard(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly top-up records for card={} year={}",
                                        response.size(), req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly top-up stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly top-up for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly top-up stats")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CardResponseYearAmount>> findYearAmountByCard(MonthYearCardNumberCard req) {
                log.info("📊 Fetching yearly top-up stats for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearAmount> amount = cardTopupAmountByCardRepository
                                        .getYearlyTopupAmountByCard(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amount.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Yearly top-up for card={} year={} retrieved successfully",
                                        req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly top-up stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly top-up for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly top-up stats")
                                        .data(null)
                                        .build();
                }
        }
}
