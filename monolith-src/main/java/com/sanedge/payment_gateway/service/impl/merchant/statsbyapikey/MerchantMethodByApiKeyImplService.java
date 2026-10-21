package com.sanedge.payment_gateway.service.impl.merchant.statsbyapikey;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearPaymentMethodApiKey;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseMonthlyPaymentMethod;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.method.MerchantResponseYearlyPaymentMethod;
import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyPaymentMethod;
import com.sanedge.payment_gateway.models.merchants.MerchantYearlyPaymentMethod;
import com.sanedge.payment_gateway.repository.merchants.statsbyapikey.MerchantMethodByApiKeyRepository;
import com.sanedge.payment_gateway.service.merchant.stats.method.MerchantMethodByApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantMethodByApiKeyImplService implements MerchantMethodByApiKeyService {
        private final MerchantMethodByApiKeyRepository merchantMethodByApiKeyRepository;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyPaymentMethod>> findMonthMethod(
                        MonthYearPaymentMethodApiKey req) {
                String apiKey = req.getApiKey();
                Long year = (long) req.getYear();

                log.info("📊 Fetching monthly payment method stats for year: {} with apiKey: {}", year, apiKey);

                try {
                        validateYear(year);

                        List<MerchantMonthlyPaymentMethod> monthlyMethods = merchantMethodByApiKeyRepository
                                        .findMonthlyPaymentMethodsByApiKey(apiKey, year);

                        List<MerchantResponseMonthlyPaymentMethod> response = monthlyMethods.stream()
                                        .map(MerchantResponseMonthlyPaymentMethod::from)
                                        .toList();

                        log.info("✅ Found {} monthly payment method records for year {} with apiKey {}",
                                        response.size(), year, apiKey);

                        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched monthly payment method stats for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly payment method stats for year={} apiKey={}", year, apiKey,
                                        e);
                        return ApiResponse.<List<MerchantResponseMonthlyPaymentMethod>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch monthly payment method stats for year=" + year)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyPaymentMethod>> findYearMethod(MonthYearPaymentMethodApiKey req) {
                String apiKey = req.getApiKey();
                Long year = (long) req.getYear();

                log.info("📊 Fetching yearly payment method stats for year: {} with apiKey: {}", year, apiKey);

                try {
                        validateYear(year);

                        List<MerchantYearlyPaymentMethod> yearlyMethods = merchantMethodByApiKeyRepository
                                        .findYearlyPaymentMethodsByApiKey(apiKey, year);

                        List<MerchantResponseYearlyPaymentMethod> response = yearlyMethods.stream()
                                        .map(MerchantResponseYearlyPaymentMethod::from)
                                        .toList();

                        log.info("✅ Found {} yearly payment method records for year {} with apiKey {}", response.size(),
                                        year, apiKey);

                        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched yearly payment method stats for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly payment method stats for year={} apiKey={}", year, apiKey,
                                        e);
                        return ApiResponse.<List<MerchantResponseYearlyPaymentMethod>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch yearly payment method stats for year=" + year)
                                        .build();
                }
        }

        private void validateYear(Long year) {
                if (year == null || year < 1 || year > 9999) {
                        log.error("❌ Invalid year: {}", year);
                        throw new IllegalArgumentException("Invalid year");
                }
        }
}
