package com.sanedge.payment_gateway.service.impl.transaction;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sanedge.payment_gateway.domain.requests.transaction.CreateTransactionRequest;
import com.sanedge.payment_gateway.domain.requests.transaction.UpdateTransactionRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponse;
import com.sanedge.payment_gateway.domain.responses.transaction.TransactionResponseDeleteAt;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.card.Card;
import com.sanedge.payment_gateway.models.merchants.Merchant;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.transaction.Transaction;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.repository.merchants.MerchantQueryRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionCommandRepository;
import com.sanedge.payment_gateway.repository.transaction.TransactionQueryRepository;
import com.sanedge.payment_gateway.service.transaction.TransactionCommandService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionCommandImplService implements TransactionCommandService {
    private final TransactionQueryRepository transactionQueryRepository;
    private final TransactionCommandRepository transactionCommandRepository;
    private final MerchantQueryRepository merchantQueryRepository;
    private final SaldoQueryRepository saldoQueryRepository;
    private final SaldoCommandRepository saldoCommandRepository;
    private final CardQueryRepository cardQueryRepository;
    private final Validator validator;

    @Override
    public ApiResponse<TransactionResponse> create(String apiKey, CreateTransactionRequest req) {
        log.info("▶️ Starting CreateTransaction process, apiKey={}, req={}", apiKey, req);

        validateRequest(req);

        try {
            Merchant merchant = merchantQueryRepository.findByApiKey(apiKey)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            Card card = cardQueryRepository.findCardByCardNumber(req.getCardNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

            Saldo saldo = saldoQueryRepository.findByCardNumber(req.getCardNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Saldo not found"));

            if (saldo.getTotalBalance() < req.getAmount()) {
                log.error("❌ Insufficient balance, requested: {}, available: {}", req.getAmount(),
                        saldo.getTotalBalance());
                throw new ResourceNotFoundException("Insufficient balance");
            }

            Long updatedSaldo = saldo.getTotalBalance() - req.getAmount();
            saldoCommandRepository.updateBalanceByCardNumber(card.getCardNumber(), updatedSaldo);

            Transaction transactionEntity = new Transaction();

            UUID transactionNo = UUID.randomUUID();

            transactionEntity.setCardNumber(req.getCardNumber());
            transactionEntity.setMerchantId(req.getMerchantId());
            transactionEntity.setAmount(req.getAmount());
            transactionEntity.setPaymentMethod(req.getPaymentMethod());
            transactionEntity.setTransactionTime(Timestamp.valueOf(LocalDateTime.now()));
            transactionEntity.setTransactionNo(transactionNo);
            transactionEntity.setStatus(Status.PENDING);

            Transaction transaction;
            try {
                transaction = transactionCommandRepository.save(transactionEntity);
            } catch (Exception e) {
                log.error("❌ Failed to create transaction, rolling back saldo", e);

                saldoCommandRepository.updateBalanceByCardNumber(card.getCardNumber(), saldo.getTotalBalance());
                throw new ResourceNotFoundException("Failed to create transaction");
            }

            transactionCommandRepository.updateTransactionStatus(transaction.getTransactionId(),
                    Status.SUCCESS.toString());

            Card merchantCard = cardQueryRepository.findCardByUserId(merchant.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant card not found"));

            Saldo merchantSaldo = saldoQueryRepository.findByCardNumber(merchantCard.getCardNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant saldo not found"));

            Long updatedMerchantSaldo = merchantSaldo.getTotalBalance() + req.getAmount();
            saldoCommandRepository.updateBalanceByCardNumber(merchantCard.getCardNumber(), updatedMerchantSaldo);

            TransactionResponse response = TransactionResponse.from(transaction);

            log.info("✅ CreateTransaction completed, transaction_id={}", response.getId());

            return ApiResponse.<TransactionResponse>builder()
                    .status("success")
                    .message("Transaction created successfully")
                    .data(response)
                    .build();

        } catch (ResourceNotFoundException e) {
            log.error("🔥 Error in create transaction: {}", e.getMessage(), e);

            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .data(null)
                    .message("Error in create transaction")
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponse> update(String apiKey, UpdateTransactionRequest req) {
        log.info("▶️ Starting UpdateTransaction process: {}", req);

        validateRequest(req);

        Long transactionId = Optional.ofNullable(req.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("transaction_id is required"));

        try {
            Transaction transaction = transactionQueryRepository.findById(transactionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction " + transactionId + " not found"));

            Merchant merchant = merchantQueryRepository.findByApiKey(apiKey)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

            if (!transaction.getMerchantId().equals(merchant.getMerchantId())) {
                log.error("🚫 Unauthorized access to transaction {}", transactionId);
                transactionCommandRepository.updateTransactionStatus(transactionId, Status.FAILED.toString());
                throw new ResourceNotFoundException("unauthorized access");
            }

            Card card = cardQueryRepository.findCardByCardNumber(transaction.getCardNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

            Saldo saldo = saldoQueryRepository.findByCardNumber(card.getCardNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Saldo not found"));

            Long restoredBalance = saldo.getTotalBalance() + transaction.getAmount();
            saldoCommandRepository.updateBalanceByCardNumber(card.getCardNumber(), restoredBalance);

            if (restoredBalance < req.getAmount()) {
                log.error("❌ Insufficient balance after restore, available={}, requested={}",
                        restoredBalance, req.getAmount());
                transactionCommandRepository.updateTransactionStatus(transactionId, Status.FAILED.toString());
                throw new ResourceNotFoundException("Insufficient balance");
            }

            Long updatedBalance = restoredBalance - req.getAmount();
            saldoCommandRepository.updateBalanceByCardNumber(card.getCardNumber(), updatedBalance);

            transaction.setAmount(req.getAmount());
            transaction.setPaymentMethod(req.getPaymentMethod());
            transaction.setTransactionTime((req.getTransactionTime() != null ? Timestamp.valueOf(req.getTransactionTime()) : new java.sql.Timestamp(System.currentTimeMillis())));

            Transaction updated = transactionCommandRepository.save(transaction);

            transactionCommandRepository.updateTransactionStatus(transactionId, Status.SUCCESS.toString());

            TransactionResponse response = TransactionResponse.from(updated);

            log.info("✅ Transaction {} updated successfully", transactionId);

            return ApiResponse.<TransactionResponse>builder()
                    .status("success")
                    .message("Transaction updated successfully")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("🔥 Error updating transaction: {}", e.getMessage(), e);
            return ApiResponse.<TransactionResponse>builder()
                    .status("error")
                    .message("Failed to update transaction")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponseDeleteAt> trashed(Long transactionId) {
        log.info("🗑️ Trashing transaction id={}", transactionId);

        try {
            Transaction transaction = transactionCommandRepository.trashed(transactionId);
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Transaction trashed successfully!")
                    .data(TransactionResponseDeleteAt.from(transaction))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash transaction id={}", transactionId, e);
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("error")
                    .message("💥 Failed to trash transaction")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<TransactionResponseDeleteAt> restore(Long transactionId) {
        log.info("♻️ Restoring transaction id={}", transactionId);

        try {
            Transaction transaction = transactionCommandRepository.restore(transactionId);
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Transaction restored successfully!")
                    .data(TransactionResponseDeleteAt.from(transaction))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore transaction id={}", transactionId, e);
            return ApiResponse.<TransactionResponseDeleteAt>builder()
                    .status("error")
                    .message("💥 Failed to restore transaction")
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long transactionId) {
        log.info("🧨 Permanently deleting transaction id={}", transactionId);

        try {
            transactionCommandRepository.deletePermanent(transactionId);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Transaction permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete transaction id={}", transactionId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to permanently delete transaction")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed transactions");

        try {
            transactionCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All transactions restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all transactions", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to restore all transactions")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed transactions");

        try {
            transactionCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All transactions permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all transactions", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to delete all transactions")
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
            log.error("Validation failed: {}", sb.toString());
            throw new ConstraintViolationException("Validation failed: " + sb, violations);
        }
    }

}