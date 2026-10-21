package com.sanedge.payment_gateway.service.impl.topup.statsbycard;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.topup.stats.YearMonthMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.topup.stats.amount.TopupYearlyAmountResponse;
import com.sanedge.payment_gateway.models.topup.TopupMonthAmount;
import com.sanedge.payment_gateway.models.topup.TopupYearAmount;
import com.sanedge.payment_gateway.repository.topup.statsbycard.TopupAmountByCardRepository;
import com.sanedge.payment_gateway.service.topup.stats.amount.TopupAmountByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupAmountByCardImplService implements TopupAmountByCardService {
    private final TopupAmountByCardRepository topupAmountByCardRepository;

    @Override
    public ApiResponse<List<TopupMonthAmountResponse>> findMonthlyAmounts(YearMonthMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();
        log.info("📊 Fetching monthly topup amounts for card={} year={}", cardNumber, year);

        validateCard(cardNumber);
        validateYear(year);

        try {
            List<TopupMonthAmount> amounts = topupAmountByCardRepository.findMonthlyTopupAmountsByCard(cardNumber,
                    year);

            log.info("✅ Retrieved {} monthly topup amount records for card={} year={}", amounts.size(), cardNumber,
                    year);

            List<TopupMonthAmountResponse> response = amounts.stream()
                    .map(TopupMonthAmountResponse::from)
                    .toList();

            return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched monthly topup amounts for card=" + cardNumber)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly topup amounts for card={} year={}", cardNumber, year, e);
            return ApiResponse.<List<TopupMonthAmountResponse>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch monthly topup amounts for card=" + cardNumber)
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TopupYearlyAmountResponse>> findYearlyAmounts(YearMonthMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();
        log.info("📊 Fetching yearly topup amounts for card={} until year={}", cardNumber, year);

        validateCard(cardNumber);
        validateYear(year);

        try {
            List<TopupYearAmount> amounts = topupAmountByCardRepository.findYearlyTopupAmountsByCard(cardNumber, year);

            log.info("✅ Retrieved {} yearly topup amount records for card={} until year={}", amounts.size(), cardNumber,
                    year);

            List<TopupYearlyAmountResponse> response = amounts.stream()
                    .map(TopupYearlyAmountResponse::from)
                    .toList();

            return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Successfully fetched yearly topup amounts for card=" + cardNumber)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly topup amounts for card={} until year={}", cardNumber, year, e);
            return ApiResponse.<List<TopupYearlyAmountResponse>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .message("Failed to fetch yearly topup amounts for card=" + cardNumber)
                    .build();
        }
    }

    private void validateCard(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            log.error("❌ Card number is required");
            throw new IllegalArgumentException("Card number is required");
        }
    }

    private void validateYear(Long year) {
        if (year == null || year < 1 || year > 9999) {
            log.error("❌ Invalid year: {}", year);
            throw new IllegalArgumentException("Invalid year");
        }
    }
}
