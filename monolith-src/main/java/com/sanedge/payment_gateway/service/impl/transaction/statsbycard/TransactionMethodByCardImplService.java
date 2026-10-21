package com.sanedge.payment_gateway.service.impl.transaction.statsbycard;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.transaction.stats.MonthYearPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionMonthMethodResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.stats.method.TransactionYearMethodResponse;
import com.sanedge.payment_gateway.models.transaction.TransactionMonthMethod;
import com.sanedge.payment_gateway.models.transaction.TransactionYearMethod;
import com.sanedge.payment_gateway.repository.transaction.statsbycard.TransactionMethodByCardRepository;
import com.sanedge.payment_gateway.service.transaction.stats.method.TransactionMethodByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionMethodByCardImplService implements TransactionMethodByCardService {
    private final TransactionMethodByCardRepository transactionMethodByCardRepository;

    @Override
    public ApiResponse<List<TransactionMonthMethodResponse>> findMonthlyMethod(MonthYearPaymentMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        log.info("📊 Fetching monthly transaction methods for cardNumber={}, year={}", cardNumber, year);

        if (!isValidYear(year) || !isValidCard(cardNumber)) {
            log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
            return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                    .status("error")
                    .message("Invalid input - year, cardNumber")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionMonthMethod> methods = transactionMethodByCardRepository
                    .findMonthlyPaymentMethodsByCard(cardNumber, year);

            List<TransactionMonthMethodResponse> response = methods.stream()
                    .map(TransactionMonthMethodResponse::from)
                    .toList();

            log.info("✅ Retrieved {} monthly transaction method records for cardNumber={}, year={}",
                    response.size(), cardNumber, year);

            return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Monthly Transactio methods retrieved successfully!")
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch monthly transaction methods for cardNumber={}, year={}", cardNumber, year, e);
            return ApiResponse.<List<TransactionMonthMethodResponse>>builder()
                    .status("error")
                    .data(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<TransactionYearMethodResponse>> findYearlyMethod(MonthYearPaymentMethod req) {
        String cardNumber = req.getCardNumber();
        Long year = (long) req.getYear();

        log.info("📊 Fetching yearly transaction methods for cardNumber={}, until year={}", cardNumber, year);

        if (!isValidYear(year) || !isValidCard(cardNumber)) {
            log.error("❌ Invalid input: year={}, cardNumber={}", year, cardNumber);
            return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                    .status("error")
                    .message("Invalid input - year, cardNumber")
                    .data(Collections.emptyList())
                    .build();
        }

        try {
            List<TransactionYearMethod> methods = transactionMethodByCardRepository
                    .findYearlyPaymentMethodsByCard(cardNumber, year);

            List<TransactionYearMethodResponse> response = methods.stream()
                    .map(TransactionYearMethodResponse::from)
                    .toList();

            log.info("✅ Retrieved {} yearly transaction method records for cardNumber={}, until year={}",
                    response.size(), cardNumber, year);

            return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                    .status("success")
                    .data(response)
                    .message("Yearly Transactio methods retrieved successfully!")
                    .build();

        } catch (Exception e) {
            log.error("💥 Failed to fetch yearly transaction methods for cardNumber={}, year={}", cardNumber, year, e);
            return ApiResponse.<List<TransactionYearMethodResponse>>builder()
                    .status("error")
                    .message("Failed to fetch yearly transaction methods for cardNumber")
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
