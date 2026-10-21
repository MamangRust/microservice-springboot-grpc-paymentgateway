package com.sanedge.payment_gateway.service.impl.withdraw.statsbycard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.withdraws.statsbycard.YearMonthCardNumber;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawMonthlyAmountResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.stats.amount.WithdrawYearlyAmountResponse;
import com.sanedge.payment_gateway.models.withdraw.WithdrawMonthlyAmount;
import com.sanedge.payment_gateway.models.withdraw.WithdrawYearlyAmount;
import com.sanedge.payment_gateway.repository.withdraw.statsbycard.WithdrawAmountByCardRepository;
import com.sanedge.payment_gateway.service.withdraw.stats.amount.WithdrawAmountByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawAmountByCardImplService implements WithdrawAmountByCardService {
        private final WithdrawAmountByCardRepository withdrawAmountByCardRepository;

        @Override
        public ApiResponse<List<WithdrawMonthlyAmountResponse>> findMonthlyByCardNumber(YearMonthCardNumber req) {
                try {
                        String cardNumber = req.getCardNumber();
                        Long year = (long) req.getYear();

                        log.info("📊 Fetching monthly withdraw amounts for card={} in year={}", cardNumber, year);

                        validateYear(year);
                        validateCardNumber(cardNumber);

                        List<WithdrawMonthlyAmount> amounts = withdrawAmountByCardRepository
                                        .findMonthlyWithdrawsByCard(cardNumber, year);

                        List<WithdrawMonthlyAmountResponse> response = amounts.stream()
                                        .map(WithdrawMonthlyAmountResponse::from)
                                        .toList();

                        return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                                        .status("success")
                                        .message("Monthly withdraw amounts by card retrieved successfully!")
                                        .data(response)
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.warn("⚠️ Invalid input: {}", e.getMessage());
                        return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                                        .status("error")
                                        .message("invalid input")
                                        .data(null)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly withdraw amounts by card", e);
                        return ApiResponse.<List<WithdrawMonthlyAmountResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly withdraw amounts by card")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<WithdrawYearlyAmountResponse>> findYearlyByCardNumber(YearMonthCardNumber req) {
                try {
                        String cardNumber = req.getCardNumber();
                        Long year = (long) req.getYear();

                        log.info("📊 Fetching yearly withdraw amounts for card={} until year={}", cardNumber, year);

                        validateYear(year);
                        validateCardNumber(cardNumber);

                        List<WithdrawYearlyAmount> amounts = withdrawAmountByCardRepository
                                        .findYearlyWithdrawsByCard(cardNumber, year);

                        List<WithdrawYearlyAmountResponse> response = amounts.stream()
                                        .map(WithdrawYearlyAmountResponse::from)
                                        .toList();

                        return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                                        .status("success")
                                        .message("Yearly withdraw amounts by card retrieved successfully!")
                                        .data(response)
                                        .build();

                } catch (IllegalArgumentException e) {
                        log.warn("⚠️ Invalid input: {}", e.getMessage());
                        return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                                        .status("error")
                                        .message("invalid input")
                                        .data(null)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly withdraw amounts by card", e);
                        return ApiResponse.<List<WithdrawYearlyAmountResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly withdraw amounts by card")
                                        .data(null)
                                        .build();
                }
        }

        private void validateYear(Long year) {
                if (year == null || year < 1 || year > 9999) {
                        throw new IllegalArgumentException("Invalid year");
                }
        }

        private void validateCardNumber(String cardNumber) {
                if (cardNumber == null || cardNumber.isBlank()) {
                        throw new IllegalArgumentException("Card number must not be null or blank");
                }
        }
}
