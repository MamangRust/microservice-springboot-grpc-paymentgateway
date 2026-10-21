package com.sanedge.payment_gateway.service.impl.card.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseMonthBalance;
import com.sanedge.payment_gateway.domain.responses.card.stats.balance.CardResponseYearBalance;
import com.sanedge.payment_gateway.models.card.CardMonthBalance;
import com.sanedge.payment_gateway.models.card.CardYearBalance;
import com.sanedge.payment_gateway.repository.card.stats.CardBalanceRepository;
import com.sanedge.payment_gateway.service.card.stats.balance.CardBalanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardBalanceImplService implements CardBalanceService {
    private final CardBalanceRepository cardBalanceRepository;

    @Override
    public ApiResponse<List<CardResponseMonthBalance>> findMonthBalance(Long year) {
        log.info("📊 Fetching monthly balance stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthBalance> balances = cardBalanceRepository.getMonthlyBalances(yearDate);

            if (balances.isEmpty()) {
                log.warn("⚠️ No monthly balance data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthBalance>>builder()
                        .status("error")
                        .message("No monthly balance stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            log.info("✅ Retrieved {} monthly balance records for year={}", balances.size(), year);

            List<CardResponseMonthBalance> response = balances.stream()
                    .map(CardResponseMonthBalance::from)
                    .toList();

            return ApiResponse.<List<CardResponseMonthBalance>>builder()
                    .status("success")
                    .message("✅ Monthly balance stats retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid year: {}", year, e);
            return ApiResponse.<List<CardResponseMonthBalance>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(List.of())
                    .build();
        } catch (Exception e) {
            log.error("❌ Failed to fetch monthly balance for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthBalance>>builder()
                    .status("error")
                    .message("Failed to fetch monthly balance stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CardResponseYearBalance>> findYearBalance(Long year) {
        log.info("📊 Fetching yearly balance stats until year={}", year);

        try {
            validateYear(year);

            List<CardYearBalance> result = cardBalanceRepository.getYearlyBalances(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly balance data found until year={}", year);
                return ApiResponse.<List<CardResponseYearBalance>>builder()
                        .status("error")
                        .message("No yearly balance stats found until year " + year)
                        .data(List.of())
                        .build();
            }

            log.info("✅ Retrieved {} yearly balance records until year={}", result.size(), year);

            List<CardResponseYearBalance> response = result.stream()
                    .map(CardResponseYearBalance::from)
                    .toList();

            return ApiResponse.<List<CardResponseYearBalance>>builder()
                    .status("success")
                    .message("✅ Yearly balance stats retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid year: {}", year, e);
            return ApiResponse.<List<CardResponseYearBalance>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(List.of())
                    .build();
        } catch (Exception e) {
            log.error("❌ Failed to fetch yearly balance until year={}", year, e);
            return ApiResponse.<List<CardResponseYearBalance>>builder()
                    .status("error")
                    .message("Failed to fetch yearly balance stats")
                    .data(List.of())
                    .build();
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            throw new IllegalArgumentException("Invalid year: " + year);
        }
    }
}
