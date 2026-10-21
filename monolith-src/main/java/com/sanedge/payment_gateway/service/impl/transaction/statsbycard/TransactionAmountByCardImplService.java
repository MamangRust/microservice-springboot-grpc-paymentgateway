package com.sanedge.payment_gateway.service.impl.transaction.statsbycard;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthYearPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionMonthAmountResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.amount.TransactionYearlyAmountResponse;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthAmount;
import com.sanedge.payment_gateway.models.transaction.TransactionYearAmount;
import com.sanedge.payment_gateway.repository.transaction.statsbycard.TransactionAmountByCardRepository;
import com.sanedge.payment_gateway.service.transaction.stats.amount.TransactionAmountByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionAmountByCardImplService implements TransactionAmountByCardService {
    private final TransactionAmountByCardRepository transactionAmountByCardRepository;

    @Override
    public ApiResponse<List<TransactionMonthAmountResponse>> findMonthlyAmounts(MonthYearPaymentMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        log.info("📊 Fetching monthly transaction amounts for cardNumber={}, year={}", cardNumber, year);

        if (!isValidYear(year) || !isValidCard(cardNumber)) {
            log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
            return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                    .status("error")
                    .message("Invalid input - year, cardNumber")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionMonthAmount> amounts = transactionAmountByCardRepository
                    .findMonthlyAmountsByCard(cardNumber, year);

            List<TransactionMonthAmountResponse> response = amounts.stream()
                    .map(TransactionMonthAmountResponse::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transaction amount records for cardNumber={}, year={}",
                    response.size(), cardNumber, year);

            return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Monthly transaction amounts for cardNumber")
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly transaction amounts for cardNumber={}, year={}", cardNumber, year, e);
            return ApiResponse.<List<TransactionMonthAmountResponse>>builder()
                    .status("error")
                    .message("Failed to fetch monthly transaction amounts")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionYearlyAmountResponse>> findYearlyAmounts(MonthYearPaymentMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        log.info("📊 Fetching yearly transaction amounts for cardNumber={}, until year={}", cardNumber, year);

        if (!isValidYear(year) || !isValidCard(cardNumber)) {
            log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
            return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Invalid input - year, cardNumber")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionYearAmount> amounts = transactionAmountByCardRepository
                    .findYearlyAmountsByCard(cardNumber, year);

            List<TransactionYearlyAmountResponse> response = amounts.stream()
                    .map(TransactionYearlyAmountResponse::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transaction amount records for cardNumber={}, until year={}",
                    response.size(), cardNumber, year);

            return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                    .status("success")
                    .message("Yearly transaction amounts for cardNumber")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly transaction amounts for cardNumber={}, year={}", cardNumber, year, e);
            return ApiResponse.<List<TransactionYearlyAmountResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transaction amounts")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    private boolean isValidYear(Long year) {
        return year != null && year >= 1 && year <= 9999;
    }

    private boolean isValidCard(String cardNumber) {
        return cardNumber != null && !cardNumber.isBlank();
    }
}
