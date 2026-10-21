package com.sanedge.payment_gateway.service.impl.card;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.card.CreateCardRequest;
import com.sanedge.payment_gateway.domain.requests.card.UpdateCardRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponse;
import com.sanedge.payment_gateway.domain.responses.card.CardResponseDeleteAt;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.repository.card.CardCommandRepository;
import com.sanedge.payment_gateway.repository.user.UserQueryRepository;
import com.sanedge.payment_gateway.service.card.CardCommandService;
import com.sanedge.payment_gateway.utils.CardNumberGenerator;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardCommandImplService implements CardCommandService {
    private final CardCommandRepository cardCommandRepository;
    private final UserQueryRepository userQueryRepository;
    private final Validator validator;

    @Override
    public ApiResponse<CardResponse> createCard(CreateCardRequest req) {
        log.info("🆕 Creating card for user_id={}", req.getUserId());

        try {
            if (!validateRequest(req)) {
                return ApiResponse.<CardResponse>builder()
                        .status("error")
                        .message("Validation failed")
                        .data(null)
                        .build();
            }

            userQueryRepository.findById(req.getUserId()).orElseThrow(() -> {
                log.error("👤 User with id {} not found", req.getUserId());
                return new IllegalArgumentException("User not found");
            });

            Card card = new Card();
            try {
                String cardNumber = CardNumberGenerator.randomCardNumber();
                card.setCardNumber(cardNumber);
            } catch (Exception e) {
                log.error("❌ Failed to generate card number", e);
                return ApiResponse.<CardResponse>builder()
                        .status("error")
                        .message("Failed to generate card number")
                        .data(null)
                        .build();
            }

            card.setUserId(req.getUserId());
            card.setCardType(req.getCardType());
            card.setExpireDate(Date.valueOf(req.getExpireDate()));
            card.setCvv(req.getCvv());
            card.setCardProvider(req.getCardProvider());
            card.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            card.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Card savedCard = cardCommandRepository.save(card);
            CardResponse response = CardResponse.from(savedCard);

            log.info("✅ Card created successfully with card_id={}", response.getId());
            return ApiResponse.<CardResponse>builder()
                    .status("success")
                    .message("Card created successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create card for user_id={}", req.getUserId(), e);
            return ApiResponse.<CardResponse>builder()
                    .status("error")
                    .message("Failed to create card")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CardResponse> updateCard(UpdateCardRequest req) {
        log.info("🔄 Updating card id={} for user_id={}", req.getCardId(), req.getUserId());

        try {
            if (!validateRequest(req)) {
                return ApiResponse.<CardResponse>builder()
                        .status("error")
                        .message("Validation failed")
                        .data(null)
                        .build();
            }

            if (req.getCardId() == null) {
                log.error("card_id is required");
                return ApiResponse.<CardResponse>builder()
                        .status("error")
                        .message("card_id is required")
                        .data(null)
                        .build();
            }

            userQueryRepository.findById(req.getUserId()).orElseThrow(() -> {
                log.error("👤 User with id {} not found", req.getUserId());
                return new IllegalArgumentException("User not found");
            });

            Card card = cardCommandRepository.findById(req.getCardId()).orElseThrow(() -> {
                log.error("💳 Card with id {} not found", req.getCardId());
                return new IllegalArgumentException("Card not found");
            });

            card.setCardType(req.getCardType());
            card.setExpireDate(Date.valueOf(req.getExpireDate()));
            card.setCvv(req.getCvv());
            card.setCardProvider(req.getCardProvider());
            card.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Card updatedCard = cardCommandRepository.save(card);
            CardResponse response = CardResponse.from(updatedCard);

            log.info("✅ Card updated successfully with card_id={}", response.getId());
            return ApiResponse.<CardResponse>builder()
                    .status("success")
                    .message("Card updated successfully")
                    .data(response)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update card id={} for user_id={}", req.getCardId(), req.getUserId(), e);
            return ApiResponse.<CardResponse>builder()
                    .status("error")
                    .message("Failed to update card")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CardResponseDeleteAt> trashCard(Long id) {
        log.info("🗑️ Trashing card id={}", id);
        try {
            Card card = cardCommandRepository.trashed(id);
            return ApiResponse.<CardResponseDeleteAt>builder()
                    .status("success")
                    .message("Card trashed successfully")
                    .data(CardResponseDeleteAt.from(card))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash card id={}", id, e);
            return ApiResponse.<CardResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash card")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<CardResponseDeleteAt> restoreCard(Long id) {
        log.info("♻️ Restoring card id={}", id);
        try {
            Card card = cardCommandRepository.restore(id);
            return ApiResponse.<CardResponseDeleteAt>builder()
                    .status("success")
                    .message("Card restored successfully")
                    .data(CardResponseDeleteAt.from(card))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore card id={}", id, e);
            return ApiResponse.<CardResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore card")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteCard(Long id) {
        log.info("🧨 Permanently deleting card id={}", id);
        try {
            cardCommandRepository.deletePermanent(id);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Card permanently deleted")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete card id={}", id, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete card")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed cards");
        try {
            cardCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All cards restored successfully")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all cards", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all cards")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed cards");
        try {
            cardCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All cards permanently deleted")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all cards", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all cards")
                    .data(false)
                    .build();
        }
    }

    private <T> boolean validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("; ");
            }
            log.error("Validation failed: {}", sb);
            return false;
        }
        return true;
    }
}
