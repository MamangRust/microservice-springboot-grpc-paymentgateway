package com.sanedge.payment_gateway.service.impl.card.statsbycard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.card.MonthYearCardNumberCard;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseMonthAmount;
import com.sanedge.payment_gateway.domain.responses.card.stats.amount.CardResponseYearAmount;
import com.sanedge.payment_gateway.models.card.CardMonthAmount;
import com.sanedge.payment_gateway.models.card.CardYearAmount;
import com.sanedge.payment_gateway.repository.card.statsbycard.CardTransferAmountByCardRepository;
import com.sanedge.payment_gateway.service.card.stats.transfer.CardTransferAmountByCardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardTransferAmountByCardImplService implements CardTransferAmountByCardService {
        private final CardTransferAmountByCardRepository cardTransferAmountByCardRepository;

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountSender(MonthYearCardNumberCard req) {
                log.info("📊 Fetching monthly transfer (sent) for card={} year={}", req.getCardNumber(), req.getYear());
                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardTransferAmountByCardRepository
                                        .getMonthlyTransferAmountBySender(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly transfer (sent) records", response.size());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly transfer (sent) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly transfer (sent) for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly transfer (sent)")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CardResponseYearAmount>> findYearAmountSender(MonthYearCardNumberCard req) {
                log.info("📊 Fetching yearly transfer (sent) for card={} year={}", req.getCardNumber(), req.getYear());

                try {
                        List<CardYearAmount> amounts = cardTransferAmountByCardRepository
                                        .getYearlyTransferAmountBySender(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amounts.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Yearly transfer (sent) retrieved successfully for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly transfer (sent) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly transfer (sent) for card={} year={}", req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly transfer (sent)")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CardResponseMonthAmount>> findMonthAmountReceiver(MonthYearCardNumberCard req) {
                log.info("📊 Fetching monthly transfer (received) for card={} year={}", req.getCardNumber(),
                                req.getYear());
                LocalDate yearDate = LocalDate.of(req.getYear().intValue(), 1, 1);

                try {
                        List<CardMonthAmount> amounts = cardTransferAmountByCardRepository
                                        .getMonthlyTransferAmountByReceiver(req.getCardNumber(), yearDate);

                        List<CardResponseMonthAmount> response = amounts.stream()
                                        .map(CardResponseMonthAmount::from)
                                        .toList();

                        log.info("✅ Retrieved {} monthly transfer (received) records", response.size());

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("success")
                                        .message("Monthly transfer (received) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch monthly transfer (received) for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseMonthAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch monthly transfer (received)")
                                        .data(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<List<CardResponseYearAmount>> findYearAmountReceiver(MonthYearCardNumberCard req) {
                log.info("📊 Fetching yearly transfer (received) for card={} year={}", req.getCardNumber(),
                                req.getYear());

                try {
                        List<CardYearAmount> amounts = cardTransferAmountByCardRepository
                                        .getYearlyTransferAmountByReceiver(req.getCardNumber(), req.getYear());

                        List<CardResponseYearAmount> response = amounts.stream()
                                        .map(CardResponseYearAmount::from)
                                        .toList();

                        log.info("✅ Yearly transfer (received) retrieved successfully for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear());

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("success")
                                        .message("Yearly transfer (received) retrieved successfully")
                                        .data(response)
                                        .build();

                } catch (Exception e) {
                        log.error("❌ Failed to fetch yearly transfer (received) for card={} year={}",
                                        req.getCardNumber(),
                                        req.getYear(), e);

                        return ApiResponse.<List<CardResponseYearAmount>>builder()
                                        .status("error")
                                        .message("Failed to fetch yearly transfer (received)")
                                        .data(null)
                                        .build();
                }
        }
}
