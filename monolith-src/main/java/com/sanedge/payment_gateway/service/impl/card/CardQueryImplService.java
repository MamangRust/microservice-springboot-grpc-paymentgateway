package com.sanedge.payment_gateway.service.impl.card;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.card.FindAllCards;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponsePagination;
import com.sanedge.payment_gateway.domain.responses.api.PaginationMeta;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponseDeleteAt;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.service.card.CardQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardQueryImplService implements CardQueryService {
        private final CardQueryRepository cardQueryRepository;

        @Override
        public ApiResponsePagination<List<CardResponse>> findAll(FindAllCards req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 1;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                        log.info("🔍 Searching all cards | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword.isEmpty() ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Card> cardPage = cardQueryRepository.findCards(keyword, pageable);

                        List<CardResponse> cardResponses = cardPage.getContent().stream()
                                        .map(CardResponse::from)
                                        .toList();

                        log.info("✅ Found {} cards", cardResponses.size());

                        return ApiResponsePagination.<List<CardResponse>>builder()
                                        .status("success")
                                        .data(cardResponses)
                                        .message("Cards retrieved successfully")
                                        .pagination(PaginationMeta.fromSpringPage(cardPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch all cards", e);
                        return ApiResponsePagination.<List<CardResponse>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .message("Failed to retrieve cards")
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<CardResponseDeleteAt>> findByActive(FindAllCards req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch()
                                        : null;

                        log.info("🔍 Fetching active cards | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword == null ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Card> cardPage = cardQueryRepository.findActiveCards(keyword, pageable);

                        List<CardResponseDeleteAt> cardResponses = cardPage.getContent().stream()
                                        .map(CardResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Retrieved {} active cards", cardResponses.size());

                        return ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                                        .status("success")
                                        .data(cardResponses)
                                        .pagination(PaginationMeta.fromSpringPage(cardPage))
                                        .message("Cards retrieved successfully")
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active cards", e);
                        return ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .message("Failed to retrieve cards")
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<CardResponseDeleteAt>> findByTrashed(FindAllCards req) {
                try {
                        int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                        int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                        String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch()
                                        : null;

                        log.info("🗑️ Fetching trashed cards | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                        keyword == null ? "None" : keyword);

                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Card> cardPage = cardQueryRepository.findTrashedCards(keyword, pageable);

                        List<CardResponseDeleteAt> cardResponses = cardPage.getContent().stream()
                                        .map(CardResponseDeleteAt::from)
                                        .toList();

                        log.info("🗑️ Found {} trashed cards", cardResponses.size());

                        return ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                                        .status("success")
                                        .data(cardResponses)
                                        .pagination(PaginationMeta.fromSpringPage(cardPage))
                                        .message("Cards retrieved successfully")
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed cards", e);
                        return ApiResponsePagination.<List<CardResponseDeleteAt>>builder()
                                        .status("error")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .message("Failed to retrieve cards")
                                        .build();
                }
        }

        @Override
        public ApiResponse<CardResponse> findById(Long cardId) {
                try {
                        log.info("🔍 Fetching card by id={}", cardId);
                        return cardQueryRepository.findById(cardId)
                                        .map(card -> ApiResponse.<CardResponse>builder()
                                                        .status("success")
                                                        .data(CardResponse.from(card))
                                                        .message("Card retrieved successfully")
                                                        .build())
                                        .orElseGet(() -> {
                                                log.error("❌ Card with id={} not found", cardId);
                                                return ApiResponse.<CardResponse>builder()
                                                                .status("error")
                                                                .data(null)
                                                                .message("Card not found")
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch card by id={}", cardId, e);
                        return ApiResponse.<CardResponse>builder()
                                        .status("error")
                                        .data(null)
                                        .message("Failed to retrieve card")
                                        .build();
                }
        }

        @Override
        public ApiResponse<CardResponse> findByUserId(Long userId) {
                try {
                        log.info("🔍 Fetching card by user_id={}", userId);
                        return cardQueryRepository.findCardByUserId(userId)
                                        .map(card -> ApiResponse.<CardResponse>builder()
                                                        .status("success")
                                                        .data(CardResponse.from(card))
                                                        .message("Card retrieved successfully")
                                                        .build())
                                        .orElseGet(() -> {
                                                log.error("❌ Card for user_id={} not found", userId);
                                                return ApiResponse.<CardResponse>builder()
                                                                .status("error")
                                                                .data(null)
                                                                .message("Card not found")
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch card by user_id={}", userId, e);
                        return ApiResponse.<CardResponse>builder()
                                        .status("error")
                                        .data(null)
                                        .message("Failed to retrieve card")
                                        .build();
                }
        }

        @Override
        public ApiResponse<CardResponse> findByCardNumber(String cardNumber) {
                try {
                        log.info("🔍 Fetching card by card_number={}", cardNumber);
                        return cardQueryRepository.findCardByCardNumber(cardNumber)
                                        .map(card -> ApiResponse.<CardResponse>builder()
                                                        .status("success")
                                                        .data(CardResponse.from(card))
                                                        .message("Card retrieved successfully")
                                                        .build())
                                        .orElseGet(() -> {
                                                log.error("❌ Card with card_number={} not found", cardNumber);
                                                return ApiResponse.<CardResponse>builder()
                                                                .status("error")
                                                                .data(null)
                                                                .message("Card not found")
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch card by card_number={}", cardNumber, e);
                        return ApiResponse.<CardResponse>builder()
                                        .status("error")
                                        .data(null)
                                        .message("Failed to retrieve card")
                                        .build();
                }
        }
}
