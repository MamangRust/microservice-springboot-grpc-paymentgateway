package com.sanedge.payment_gateway.service.impl.merchant.statsbyapikey;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.merchant.statsbyapikey.MonthYearAmountApiKey;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseMonthlyAmount;
import com.sanedge.payment_gateway.domain.responses.merchant.stats.amount.MerchantResponseYearlyAmount;
import com.sanedge.payment_gateway.models.merchants.MerchantMonthlyAmount;
import com.sanedge.payment_gateway.models.merchants.MerchantYearlyAmount;
import com.sanedge.payment_gateway.repository.merchants.statsbyapikey.MerchantAmountByApiKeyRepository;
import com.sanedge.payment_gateway.service.merchant.stats.amount.MerchantAmountByApiKeyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantAmountByApiKeyImplService implements MerchantAmountByApiKeyService {
        private final MerchantAmountByApiKeyRepository merchantAmountByApiKeyRepository;

        @Override
        public ApiResponse<List<MerchantResponseMonthlyAmount>> findMonthAmountByApiKey(MonthYearAmountApiKey req) {
                String apiKey = req.getApiKey();
                Long year = (long) req.getYear();

                log.info("📊 Fetching monthly merchant amounts for year: {} with apiKey: {}", year, apiKey);

                try {
                        validateYear(year);

                        List<MerchantMonthlyAmount> monthlyAmounts = merchantAmountByApiKeyRepository
                                        .findMonthlyAmountByApiKey(apiKey, year);

                        List<MerchantResponseMonthlyAmount> response = monthlyAmounts.stream()
                                        .map(MerchantResponseMonthlyAmount::from)
                                        .toList();

                        log.info("✅ Found {} monthly amount records for year {} with apiKey {}", response.size(), year,
                                        apiKey);

                        return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched monthly merchant amounts for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly merchant amounts for year={} apiKey={}", year, apiKey, e);
                        return ApiResponse.<List<MerchantResponseMonthlyAmount>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch monthly merchant amounts for year=" + year)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<MerchantResponseYearlyAmount>> findYearAmountByApiKey(MonthYearAmountApiKey req) {
                String apiKey = req.getApiKey();
                Long year = (long) req.getYear();

                log.info("📊 Fetching yearly merchant amounts for year: {} with apiKey: {}", year, apiKey);

                try {
                        validateYear(year);

                        List<MerchantYearlyAmount> yearlyAmounts = merchantAmountByApiKeyRepository
                                        .findYearlyAmountByApiKey(apiKey, year);

                        List<MerchantResponseYearlyAmount> response = yearlyAmounts.stream()
                                        .map(MerchantResponseYearlyAmount::from)
                                        .toList();

                        log.info("✅ Found {} yearly amount records for year {} with apiKey {}", response.size(), year,
                                        apiKey);

                        return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                                        .status("success")
                                        .data(response)
                                        .message("Successfully fetched yearly merchant amounts for year=" + year)
                                        .build();
                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly merchant amounts for year={} apiKey={}", year, apiKey, e);
                        return ApiResponse.<List<MerchantResponseYearlyAmount>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to fetch yearly merchant amounts for year=" + year)
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
