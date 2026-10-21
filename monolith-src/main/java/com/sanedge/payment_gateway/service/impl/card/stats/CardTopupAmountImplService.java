package com.sanedge.payment_gateway.service.impl.card.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.payment_gateway.models.card.CardMonthAmount;
import com.sanedge.payment_gateway.models.card.CardYearAmount;
import com.sanedge.payment_gateway.repository.card.stats.CardTopupAmountRepository;
import com.sanedge.payment_gateway.service.card.stats.topup.CardTopupAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTopupAmountImplService implements CardTopupAmountService {
    private final CardTopupAmountRepository cardTopupAmountRepository;

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmount(Long year) {
        log.info("📊 Fetching monthly top-up stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> balances = cardTopupAmountRepository.getMonthlyTopupAmount(yearDate);

            if (balances.isEmpty()) {
                log.warn("⚠️ No monthly top-up data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly top-up stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            log.info("✅ Retrieved {} monthly top-up records for year={}", balances.size(), year);

            List<CardResponseMonthAmount> response = balances.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly top-up stats retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid year: {}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(List.of())
                    .build();
        } catch (Exception e) {
            log.error("❌ Failed to fetch monthly top-up for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly top-up stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmount(Long year) {
        log.info("📊 Fetching yearly top-up stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTopupAmountRepository.getYearlyTopupAmount(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly top-up data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly top-up stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            log.info("✅ Retrieved {} yearly top-up records for year={}", result.size(), year);

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly top-up stats retrieved successfully!")
                    .data(response)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid year: {}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Invalid year provided")
                    .data(List.of())
                    .build();
        } catch (Exception e) {
            log.error("❌ Failed to fetch yearly top-up for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly top-up stats")
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
