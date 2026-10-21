package com.sanedge.payment_gateway.service.impl.card.stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.payment_gateway.models.card.CardMonthAmount;
import com.sanedge.payment_gateway.models.card.CardYearAmount;
import com.sanedge.payment_gateway.repository.card.stats.CardTransferAmountRepository;
import com.sanedge.payment_gateway.service.card.stats.transfer.CardTransferAmountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTransferAmountImplService implements CardTransferAmountService {
    private final CardTransferAmountRepository cardTransferAmountRepository;

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(Long year) {
        log.info("📊 Fetching monthly transfer sender stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> transfers = cardTransferAmountRepository.getMonthlyTransferAmountSender(yearDate);

            if (transfers.isEmpty()) {
                log.warn("⚠️ No monthly transfer sender data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly transfer sender stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseMonthAmount> response = transfers.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transfer sender records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly transfer sender stats retrieved successfully!")
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
            log.error("❌ Failed to fetch monthly transfer sender for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transfer sender stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(Long year) {
        log.info("📊 Fetching yearly transfer sender stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTransferAmountRepository.getYearlyTransferAmountSender(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly transfer sender data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly transfer sender stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transfer sender records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly transfer sender stats retrieved successfully!")
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
            log.error("❌ Failed to fetch yearly transfer sender for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transfer sender stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(Long year) {
        log.info("📊 Fetching monthly transfer receiver stats for year={}", year);

        try {
            validateYear(year);

            LocalDate yearDate = LocalDate.of(year.intValue(), 1, 1);
            List<CardMonthAmount> transfers = cardTransferAmountRepository.getMonthlyTransferAmountReceiver(yearDate);

            if (transfers.isEmpty()) {
                log.warn("⚠️ No monthly transfer receiver data found for year={}", year);
                return ApiResponse.<List<CardResponseMonthAmount>>builder()
                        .status("error")
                        .message("No monthly transfer receiver stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseMonthAmount> response = transfers.stream()
                    .map(CardResponseMonthAmount::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transfer receiver records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("success")
                    .message("✅ Monthly transfer receiver stats retrieved successfully!")
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
            log.error("❌ Failed to fetch monthly transfer receiver for year={}", year, e);
            return ApiResponse.<List<CardResponseMonthAmount>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transfer receiver stats")
                    .data(List.of())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(Long year) {
        log.info("📊 Fetching yearly transfer receiver stats for year={}", year);

        try {
            validateYear(year);

            List<CardYearAmount> result = cardTransferAmountRepository.getYearlyTransferAmountReceiver(year);

            if (result.isEmpty()) {
                log.warn("⚠️ No yearly transfer receiver data found for year={}", year);
                return ApiResponse.<List<CardResponseYearAmount>>builder()
                        .status("error")
                        .message("No yearly transfer receiver stats found for year " + year)
                        .data(List.of())
                        .build();
            }

            List<CardResponseYearAmount> response = result.stream()
                    .map(CardResponseYearAmount::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transfer receiver records for year={}", response.size(), year);

            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("success")
                    .message("✅ Yearly transfer receiver stats retrieved successfully!")
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
            log.error("❌ Failed to fetch yearly transfer receiver for year={}", year, e);
            return ApiResponse.<List<CardResponseYearAmount>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transfer receiver stats")
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
