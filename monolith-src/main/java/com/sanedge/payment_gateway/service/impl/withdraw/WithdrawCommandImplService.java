package com.sanedge.payment_gateway.service.impl.withdraw;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.withdraws.CreateWithdrawRequest;
import com.sanedge.payment_gateway.domain.requests.withdraws.UpdateWithdrawRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponse;
import com.sanedge.payment_gateway.domain.responses.withdraw.WithdrawResponseDeleteAt;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.withdraw.Withdraw;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawCommandRepository;
import com.sanedge.payment_gateway.repository.withdraw.WithdrawQueryRepository;
import com.sanedge.payment_gateway.service.withdraw.WithdrawCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawCommandImplService implements WithdrawCommandService {
    private final WithdrawQueryRepository withdrawQueryRepository;
    private final WithdrawCommandRepository withdrawCommandRepository;
    private final CardQueryRepository cardQueryRepository;
    private final SaldoQueryRepository saldoQueryRepository;
    private final SaldoCommandRepository saldoCommandRepository;
    private final Validator validator;

    @Override
    public ApiResponse<WithdrawResponse> create(CreateWithdrawRequest req) {
        try {
            validateRequest(req);

            if (req.getWithdrawAmount() == null || req.getWithdrawAmount() <= 0) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("withdraw_amount must be > 0")
                        .data(null)
                        .build();
            }

            var card = cardQueryRepository.findCardByCardNumber(req.getCardNumber());
            if (card.isEmpty()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Card not found")
                        .data(null)
                        .build();
            }

            var saldoOpt = saldoQueryRepository.findByCardNumber(req.getCardNumber());
            if (saldoOpt.isEmpty()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Saldo not found")
                        .data(null)
                        .build();
            }

            var saldo = saldoOpt.get();
            if (saldo.getTotalBalance() < req.getWithdrawAmount()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Insufficient balance")
                        .data(null)
                        .build();
            }

            saldo.setTotalBalance(saldo.getTotalBalance() - req.getWithdrawAmount());
            saldo.setWithdrawAmount(req.getWithdrawAmount());
            saldo.setWithdrawTime((req.getWithdrawTime() != null ? Timestamp.valueOf(req.getWithdrawTime()) : new java.sql.Timestamp(System.currentTimeMillis())));
            saldoCommandRepository.updateWithdrawByCardNumber(saldo.getCardNumber(), req.getWithdrawAmount());

            Withdraw withdraw = new Withdraw();
            withdraw.setCardNumber(req.getCardNumber());
            withdraw.setWithdrawNo(UUID.randomUUID());
            withdraw.setStatus(Status.PENDING);
            withdraw.setWithdrawTime((req.getWithdrawTime() != null ? Timestamp.valueOf(req.getWithdrawTime()) : new java.sql.Timestamp(System.currentTimeMillis())));
            withdraw.setWithdrawAmount(req.getWithdrawAmount());

            Withdraw created = withdrawCommandRepository.save(withdraw);

            Withdraw updated = withdrawCommandRepository.updateStatus(created.getWithdrawId(), Status.SUCCESS.toString());

            return ApiResponse.<WithdrawResponse>builder()
                    .status("success")
                    .message("Created withdraw successfully")
                    .data(WithdrawResponse.from(updated))
                    .build();

        } catch (Exception e) {
            log.error("💥 Error creating withdraw", e);
            return ApiResponse.<WithdrawResponse>builder()
                    .status("error")
                    .message("Failed to create withdraw")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<WithdrawResponse> update(UpdateWithdrawRequest req) {
        try {
            validateRequest(req);

            if (req.getWithdrawId() == null) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("withdraw_id is required")
                        .data(null)
                        .build();
            }

            if (req.getWithdrawAmount() == null || req.getWithdrawAmount() <= 0) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("withdraw_amount must be > 0")
                        .data(null)
                        .build();
            }

            var card = cardQueryRepository.findCardByCardNumber(req.getCardNumber());
            if (card.isEmpty()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Card not found")
                        .data(null)
                        .build();
            }

            var withdrawOpt = withdrawQueryRepository.findById(req.getWithdrawId());
            if (withdrawOpt.isEmpty()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Withdraw not found")
                        .data(null)
                        .build();
            }

            var saldoOpt = saldoQueryRepository.findByCardNumber(req.getCardNumber());
            if (saldoOpt.isEmpty()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Saldo not found")
                        .data(null)
                        .build();
            }

            var saldo = saldoOpt.get();
            if (saldo.getTotalBalance() < req.getWithdrawAmount()) {
                return ApiResponse.<WithdrawResponse>builder()
                        .status("error")
                        .message("Insufficient balance")
                        .data(null)
                        .build();
            }

            saldo.setTotalBalance(saldo.getTotalBalance() - req.getWithdrawAmount());
            saldo.setWithdrawAmount(req.getWithdrawAmount());
            saldo.setWithdrawTime((req.getWithdrawTime() != null ? Timestamp.valueOf(req.getWithdrawTime()) : new java.sql.Timestamp(System.currentTimeMillis())));
            saldoCommandRepository.updateWithdrawByCardNumber(saldo.getCardNumber(), req.getWithdrawAmount());

            var withdraw = withdrawOpt.get();
            withdraw.setCardNumber(req.getCardNumber());
            withdraw.setWithdrawAmount(req.getWithdrawAmount());
            withdraw.setWithdrawTime((req.getWithdrawTime() != null ? Timestamp.valueOf(req.getWithdrawTime()) : new java.sql.Timestamp(System.currentTimeMillis())));

            Withdraw updated = withdrawCommandRepository.save(withdraw);
            Withdraw refreshed = withdrawCommandRepository.updateStatus(updated.getWithdrawId(), Status.SUCCESS.toString());

            return ApiResponse.<WithdrawResponse>builder()
                    .status("success")
                    .message("Updated withdraw successfully")
                    .data(WithdrawResponse.from(refreshed))
                    .build();

        } catch (Exception e) {
            log.error("💥 Error updating withdraw", e);
            return ApiResponse.<WithdrawResponse>builder()
                    .status("error")
                    .message("Failed to update withdraw")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<WithdrawResponseDeleteAt> trashed(Long withdrawId) {
        try {
            Withdraw withdraw = withdrawCommandRepository.trashed(withdrawId);
            return ApiResponse.<WithdrawResponseDeleteAt>builder()
                    .status("success")
                    .message("Withdraw trashed successfully!")
                    .data(WithdrawResponseDeleteAt.from(withdraw))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash withdraw id={}", withdrawId, e);
            return ApiResponse.<WithdrawResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash withdraw")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<WithdrawResponseDeleteAt> restore(Long withdrawId) {
        try {
            Withdraw withdraw = withdrawCommandRepository.restore(withdrawId);
            return ApiResponse.<WithdrawResponseDeleteAt>builder()
                    .status("success")
                    .message("Withdraw restored successfully!")
                    .data(WithdrawResponseDeleteAt.from(withdraw))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore withdraw id={}", withdrawId, e);
            return ApiResponse.<WithdrawResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore withdraw")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long withdrawId) {
        try {
            withdrawCommandRepository.deletePermanent(withdrawId);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("Withdraw permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete withdraw id={}", withdrawId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete withdraw")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        try {
            withdrawCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All withdraws restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all withdraws", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all withdraws")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        try {
            withdrawCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("All withdraws permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all withdraws", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all withdraws")
                    .data(false)
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
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }
}
