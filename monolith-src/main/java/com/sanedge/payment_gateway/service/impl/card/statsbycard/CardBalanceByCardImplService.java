package com.sanedge.payment_gateway.service.impl.card.statsbycard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseYearBalance;
import com.sanedge.payment_gateway.models.card.CardMonthBalance;
import com.sanedge.payment_gateway.models.card.CardYearBalance;
import com.sanedge.payment_gateway.repository.card.statsbycard.CardBalanceByCardRepository;
import com.sanedge.payment_gateway.service.card.stats.balance.CardBalanceByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardBalanceByCardImplService implements CardBalanceByCardService {
        private final CardBalanceByCardRepository cardBalanceByCardRepository;

        @Override
        public ApiResponse<List<CardResponseMonthBalance>> findMonthBalanceByCard(MonthYearCardNumberCard req) {
                log.info("📊 Fetching monthly balance stats for card={} year={}", req.getCardNumber(), req.getYear());

                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthBalance> balances = cardBalanceByCardRepository.getMonthlyBalancesByCard(yearDate,
                                        req.getCardNumber());

                        List<CardResponseMonthBalance> response = balances.stream()
                                        .map(CardResponseMonthBalance::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly balance records for card={} year={}",
                                        response.size(), req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseMonthBalance>>builder()
                                        .status("success")
                                        .message("Monthly balance stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly balance for card={} year={}",
                                        req.getCardNumber(), req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthBalance>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly balance stats")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CardResponseYearBalance>> findYearBalanceByCard(MonthYearCardNumberCard req) {
                log.info("📊 Fetching yearly balance stats for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearBalance> balance = cardBalanceByCardRepository.getYearlyBalancesByCard(
                                        req.getYear(),
                                        req.getCardNumber());

                        List<CardResponseYearBalance> response = balance.stream()
                                        .map(CardResponseYearBalance::from)
                                        .toList();

                        log.info("✅ Yearly balance for card={} year={} retrieved successfully",
                                        req.getCardNumber(), req.getYear());

                        return ApiResponse.<List<CardResponseYearBalance>>builder()
                                        .status("success")
                                        .message("Yearly balance stats retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly balance for card={} year={}",
                                        req.getCardNumber(), req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearBalance>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly balance stats")
                                        .data(null)
                                        .build();
                }
        }
}
