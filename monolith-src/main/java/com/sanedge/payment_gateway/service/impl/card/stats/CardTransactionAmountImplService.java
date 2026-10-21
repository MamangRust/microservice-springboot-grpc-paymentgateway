package com.sanedge.payment_gateway.service.impl.card.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.payment_gateway.models.card.CardMonthAmount;
import com.sanedge.payment_gateway.models.card.CardYearAmount;
import com.sanedge.payment_gateway.repository.card.stats.CardTransactionAmountRepository;
import com.sanedge.payment_gateway.service.card.stats.transaction.CardTransactionAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTransactionAmountImplService implements CardTransactionAmountService {
    private final CardTransactionAmountRepository cardTransactionAmountRepository;

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmount(Long year) {
        log.info("📊 Fetching monthly transaction stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> transactions = cardTransactionAmountRepository.getMonthlyTransactionAmount(yearDate);

            if (transactions.isEmpty()) {
                log.warn("⚠️ No monthly transaction data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly transaction stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseMonthAmount> response = transactions.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transaction records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly transaction stats retrieved successfully!")
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
            log.error("❌ Failed to fetch monthly transaction for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transaction stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmount(Long year) {
        log.info("📊 Fetching yearly transaction stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTransactionAmountRepository.getYearlyTransactionAmount(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly transaction data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly transaction stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transaction records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly transaction stats retrieved successfully!")
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
            log.error("❌ Failed to fetch yearly transaction for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transaction stats")
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
