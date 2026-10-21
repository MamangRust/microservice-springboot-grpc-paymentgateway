package com.sanedge.payment_gateway.service.impl.topup;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.topup.CreateTopupRequest;
import com.sanedge.payment_gateway.domain.requests.topup.UpdateTopupRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponse;
import com.sanedge.payment_gateway.domain.responses.topup.TopupResponseDeleteAt;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.topup.Topup;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository;
import com.sanedge.payment_gateway.repository.topup.TopupCommandRepository;
import com.sanedge.payment_gateway.repository.topup.TopupQueryRepository;
import com.sanedge.payment_gateway.service.topup.TopupCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TopupCommandImplService implements TopupCommandService {
    private final CardQueryRepository cardQueryRepository;
    private final SaldoQueryRepository saldoQueryRepository;
    private final SaldoCommandRepository saldoCommandRepository;
    private final TopupQueryRepository topupQueryRepository;
    private final TopupCommandRepository topupCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<TopupResponse> create(CreateTopupRequest req) {
        log.info("🚀 Starting CreateTopup: {}", req);

        try {
            validateRequest(req);

            cardQueryRepository.findCardByCardNumber(req.getCardNumber())
                    .orElseThrow(() -> {
                        log.error("❌ Card not found: {}", req.getCardNumber());
                        return new ResourceNotFoundException("Card not found");
                    });

            UUID topupNo = UUID.randomUUID();
            Topup topup = new Topup();
            topup.setTopupNo(topupNo);
            topup.setCardNumber(req.getCardNumber());
            topup.setTopupAmount(req.getTopupAmount());
            topup.setTopupMethod(req.getTopupMethod());
            topup.setStatus(Status.PENDING);
            topup.setTopupTime(Timestamp.valueOf(LocalDateTime.now()));
            topup.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            topup.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            Topup savedTopup = topupCommandRepository.save(topup);

            Saldo saldo = saldoQueryRepository.findByCardNumber(req.getCardNumber())
                    .orElseThrow(() -> {
                        log.error("❌ Saldo not found for card: {}", req.getCardNumber());
                        topupCommandRepository.updateTopupStatus(savedTopup.getTopupId(), "FAILED");
                        return new ResourceNotFoundException("Saldo not found");
                    });

            Long newBalance = saldo.getTotalBalance() + req.getTopupAmount();
            saldo.setTotalBalance(newBalance);
            saldoCommandRepository.updateBalanceByCardNumber(saldo.getCardNumber(), newBalance);

            savedTopup.setStatus(Status.SUCCESS);
            savedTopup.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            topupCommandRepository.save(savedTopup);

            TopupResponse response = TopupResponse.from(savedTopup);

            log.info("✅ CreateTopup completed: card={} topup_amount={} new_balance={}",
                    req.getCardNumber(), req.getTopupAmount(), newBalance);

            return ApiResponse.<TopupResponse>builder()
                    .status("success")
                    .data(response)
                    .message("Topup created successfully for card=" + req.getCardNumber())
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to create topup for card={}", req.getCardNumber(), e);
            return ApiResponse.<TopupResponse>builder()
                    .status("error")
                    .data(null)
                    .message("Failed to create topup for card=" + req.getCardNumber())
                    .build();
        }
    }

    @Override
    public ApiResponse<TopupResponse> update(UpdateTopupRequest req) {
        log.info("🚀 Starting UpdateTopup: {}", req);

        try {
            validateRequest(req);

            Long topupId = req.getTopupId();
            if (topupId == null) {
                log.error("topup_id is required");
                return ApiResponse.<TopupResponse>builder().status("error").data(null).build();
            }

            cardQueryRepository.findCardByCardNumber(req.getCardNumber())
                    .orElseThrow(() -> {
                        log.error("❌ Card not found: {}", req.getCardNumber());
                        topupCommandRepository.updateTopupStatus(topupId, Status.FAILED.toString());
                        return new ResourceNotFoundException("Card not found");
                    });

            Topup existingTopup = topupQueryRepository.findById(topupId)
                    .orElseThrow(() -> {
                        log.error("❌ Topup {} not found", topupId);
                        topupCommandRepository.updateTopupStatus(topupId, Status.FAILED.toString());
                        return new ResourceNotFoundException("Topup not found");
                    });

            Long difference = req.getTopupAmount() - existingTopup.getTopupAmount();
            existingTopup.setTopupAmount(req.getTopupAmount());
            existingTopup.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            topupCommandRepository.save(existingTopup);

            Saldo saldo = saldoQueryRepository.findByCardNumber(req.getCardNumber())
                    .orElseThrow(() -> {
                        log.error("❌ Saldo not found for card: {}", req.getCardNumber());
                        topupCommandRepository.updateTopupStatus(topupId, Status.FAILED.toString());
                        return new ResourceNotFoundException("Saldo not found");
                    });

            Long newBalance = saldo.getTotalBalance() + difference;
            saldo.setTotalBalance(newBalance);
            saldoCommandRepository.updateBalanceByCardNumber(saldo.getCardNumber(), newBalance);

            existingTopup.setStatus(Status.SUCCESS);
            existingTopup.setTopupMethod(req.getTopupMethod());
            existingTopup.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            topupCommandRepository.save(existingTopup);

            TopupResponse response = TopupResponse.from(existingTopup);
            log.info("✅ UpdateTopup completed: card={} topup_id={} new_amount={} new_balance={}",
                    req.getCardNumber(), topupId, req.getTopupAmount(), newBalance);

            return ApiResponse.<TopupResponse>builder()
                    .status("success")
                    .data(response)
                    .message("Topup updated successfully for card=" + req.getCardNumber())
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to update topup for card={}", req.getCardNumber(), e);
            return ApiResponse.<TopupResponse>builder()
                    .status("error")
                    .data(null)
                    .message("Failed to update topup for card=" + req.getCardNumber())
                    .build();
        }
    }

    @Override
    public ApiResponse<TopupResponseDeleteAt> trashed(Long topupId) {
        log.info("🗑️ Trashing topup id={}", topupId);
        try {
            Topup topup = topupCommandRepository.trashed(topupId);
            return ApiResponse.<TopupResponseDeleteAt>builder()
                    .status("success")
                    .data(TopupResponseDeleteAt.from(topup))
                    .message("Trashed topup id=" + topupId)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash topup id={}", topupId, e);
            return ApiResponse.<TopupResponseDeleteAt>builder()
                    .status("error")
                    .data(null)
                    .message("Failed to trash topup id=" + topupId)
                    .build();
        }
    }

    @Override
    public ApiResponse<TopupResponseDeleteAt> restore(Long topupId) {
        log.info("♻️ Restoring topup id={}", topupId);
        try {
            Topup topup = topupCommandRepository.restore(topupId);
            return ApiResponse.<TopupResponseDeleteAt>builder()
                    .status("success")
                    .data(TopupResponseDeleteAt.from(topup))
                    .message("Restored topup id=" + topupId)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore topup id={}", topupId, e);
            return ApiResponse.<TopupResponseDeleteAt>builder()
                    .status("error")
                    .data(null)
                    .message("Failed to restore topup id=" + topupId)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long topupId) {
        log.info("🧨 Permanently deleting topup id={}", topupId);
        try {
            topupCommandRepository.deletePermanent(topupId);
            return ApiResponse.<Boolean>builder().status("success").data(true).message("Deleted topup id=" + topupId)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete topup id={}", topupId, e);
            return ApiResponse.<Boolean>builder().status("error").data(false)
                    .message("Failed to delete topup id=" + topupId).build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed topups");
        try {
            topupCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder().status("success").data(true).message("Restored all trashed topups")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all topups", e);
            return ApiResponse.<Boolean>builder().status("error").data(false).message("Failed to restore all topups")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed topups");
        try {
            topupCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder().status("success").data(true).message("Deleted all trashed topups")
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all topups", e);
            return ApiResponse.<Boolean>builder().status("error").data(false).message("Failed to delete all topups")
                    .build();
        }
    }

    private <T> void validateRequest(T req) {
        Set<ConstraintViolation<T>> violations = validator.validate(req);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Validation failed: {}", sb.toString());
            throw new ConstraintViolationException("Validation failed", violations);
        }
    }
}
