package com.sanedge.payment_gateway.service.impl.transfer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanedge.payment_gateway.domain.requests.transfers.CreateTransferRequest;
import com.sanedge.payment_gateway.domain.requests.transfers.UpdateTransferRequest;
import com.sanedge.payment_gateway.domain.responses.api.ApiResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponse;
import com.sanedge.payment_gateway.domain.responses.transfer.TransferResponseDeleteAt;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.exception.ResourceNotFoundException;
import com.sanedge.payment_gateway.models.saldo.Saldo;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.repository.card.CardQueryRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoCommandRepository;
import com.sanedge.payment_gateway.repository.saldo.SaldoQueryRepository;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;
import com.sanedge.payment_gateway.repository.transfer.TransferQueryRepository;
import com.sanedge.payment_gateway.service.transfer.TransferCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransferCommandImplService implements TransferCommandService {
    private final TransferCommandRepository transferCommandRepository;
    private final CardQueryRepository cardQueryRepository;
    private final  SaldoQueryRepository saldoQueryRepository;
    private final SaldoCommandRepository saldoCommandRepository;
    private final TransferQueryRepository transferQueryRepository;

    @Override
    @Transactional
    public ApiResponse<TransferResponse> create(CreateTransferRequest req) {
        log.info("🚀 Starting create transfer: {}", req);

        if (cardQueryRepository.findCardByCardNumber(req.getTransferFrom()).isEmpty()) {
            log.error("❌ sender card {} not found", req.getTransferFrom());
            return ApiResponse.<TransferResponse>builder()
                    .status("error")
                    .message("Sender card not found")
                    .build();
        }
        if (cardQueryRepository.findCardByCardNumber(req.getTransferTo()).isEmpty()) {
            log.error("❌ receiver card {} not found", req.getTransferTo());
            return ApiResponse.<TransferResponse>builder()
                    .status("error")
                    .message("Receiver card not found")
                    .build();
        }

        Saldo senderSaldo = saldoQueryRepository.findByCardNumber(req.getTransferFrom())
                .orElseThrow(() -> {
                    log.error("❌ failed to fetch sender saldo");
                    return new ResourceNotFoundException("Sender saldo not found");
                });

        Saldo receiverSaldo = saldoQueryRepository.findByCardNumber(req.getTransferTo())
                .orElseThrow(() -> {
                    log.error("❌ failed to fetch receiver saldo");
                    return new ResourceNotFoundException("Receiver saldo not found");
                });

        if (senderSaldo.getTotalBalance() < req.getTransferAmount()) {
            log.error("❌ insufficient balance, requested={}, available={}",
                    req.getTransferAmount(), senderSaldo.getTotalBalance());
            return ApiResponse.<TransferResponse>builder()
                    .status("error")
                    .message("Insufficient balance")
                    .build();
        }

        try {
            senderSaldo.setTotalBalance(senderSaldo.getTotalBalance() - req.getTransferAmount());
            saldoCommandRepository.updateBalanceByCardNumber(senderSaldo.getCardNumber(),
                    senderSaldo.getTotalBalance());

            receiverSaldo.setTotalBalance(receiverSaldo.getTotalBalance() + req.getTransferAmount());
            saldoCommandRepository.updateBalanceByCardNumber(receiverSaldo.getCardNumber(),
                    receiverSaldo.getTotalBalance());

            Transfer transferEntity = new Transfer();

            UUID transferNo = UUID.randomUUID();

            transferEntity.setTransferNo(transferNo);
            transferEntity.setTransferFrom(req.getTransferFrom());
            transferEntity.setTransferAmount(req.getTransferAmount());
            transferEntity.setTransferTo(req.getTransferTo());
            transferEntity.setTransferTime(Timestamp.valueOf(LocalDateTime.now()));
            transferEntity.setStatus(Status.PENDING);

            Transfer transfer = transferCommandRepository.save(transferEntity);

            transferCommandRepository.updateTransferStatus(transfer.getTransferId(), Status.SUCCESS.toString());

            log.info("✅ Transfer created successfully with ID={}", transfer.getTransferId());
            return ApiResponse.<TransferResponse>builder()
                    .status("success")
                    .message("Transfer created successfully")
                    .data(TransferResponse.from(transfer))
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to create transfer, rolling back balances", e);

            try {
                senderSaldo.setTotalBalance(senderSaldo.getTotalBalance() + req.getTransferAmount());
                saldoCommandRepository.updateBalanceByCardNumber(senderSaldo.getCardNumber(),
                        senderSaldo.getTotalBalance());
            } catch (Exception rb) {
                log.error("⚠️ Failed to rollback sender saldo", rb);
            }
            try {
                receiverSaldo.setTotalBalance(receiverSaldo.getTotalBalance() - req.getTransferAmount());
                saldoCommandRepository.updateBalanceByCardNumber(senderSaldo.getCardNumber(),
                        senderSaldo.getTotalBalance());
            } catch (Exception rb) {
                log.error("⚠️ Failed to rollback receiver saldo", rb);
            }
            return ApiResponse.<TransferResponse>builder()
                    .status("error")
                    .message("Failed to create transfer")
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse<TransferResponse> update(UpdateTransferRequest req) {
        log.info("🔄 Starting update transaction process: {}", req);

        if (req.getTransferId() <= 0) {
            throw new IllegalArgumentException("transfer_id is required");
        }

        var transfer = transferQueryRepository.findTransferById(req.getTransferId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transfer " + req.getTransferId() + " not found"));

        Long amountDifference = req.getTransferAmount() - transfer.getTransferAmount();

        try {
            var senderSaldo = saldoQueryRepository.findByCardNumber(transfer.getTransferFrom())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sender card " + transfer.getTransferFrom() + " not found"));

            Long newSenderBalance = senderSaldo.getTotalBalance() - amountDifference;
            if (newSenderBalance < 0) {
                log.error("❌ Insufficient balance for sender {}", transfer.getTransferFrom());
                transferCommandRepository.updateTransferStatus(req.getTransferId(),
                        Status.FAILED.toString());
                throw new IllegalStateException("Insufficient balance");
            }

            senderSaldo.setTotalBalance(newSenderBalance);
            saldoCommandRepository.updateBalanceByCardNumber(senderSaldo.getCardNumber(),
                    senderSaldo.getTotalBalance());

            var receiverSaldo = saldoQueryRepository.findByCardNumber(transfer.getTransferTo())
                    .orElseThrow(() -> {
                        rollbackSenderSaldo(senderSaldo, amountDifference);
                        transferCommandRepository.updateTransferStatus(req.getTransferId(),
                                Status.FAILED.toString());
                        return new ResourceNotFoundException(
                                "Receiver card " + transfer.getTransferTo() + " not found");
                    });

            receiverSaldo.setTotalBalance(receiverSaldo.getTotalBalance() + amountDifference);
            try {
                saldoCommandRepository.updateBalanceByCardNumber(receiverSaldo.getCardNumber(),
                        receiverSaldo.getTotalBalance());
            } catch (Exception e) {
                log.error("❌ Failed to update receiver saldo, rolling back..");
                rollbackSenderSaldo(senderSaldo, amountDifference);
                rollbackReceiverSaldo(receiverSaldo, amountDifference);
                transferCommandRepository.updateTransferStatus(req.getTransferId(),
                        Status.FAILED.toString());
                return ApiResponse.<TransferResponse>builder()
                        .status("error")
                        .message("Failed to update transfer")
                        .build();
            }

            transfer.setTransferAmount(req.getTransferAmount());
            transfer.setTransferFrom(req.getTransferFrom());
            transfer.setTransferTo(req.getTransferTo());
            var updatedTransfer = transferCommandRepository.save(transfer);

            transferCommandRepository.updateTransferStatus(req.getTransferId(), Status.SUCCESS.toString());

            log.info("✅ Successfully updated transfer {}", req.getTransferId());

            return ApiResponse.<TransferResponse>builder()
                    .status("success")
                    .message("Transfer updated successfully")
                    .data(TransferResponse.from(updatedTransfer))
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to update transfer {}", req.getTransferId(), e);
            return ApiResponse.<TransferResponse>builder()
                    .status("error")
                    .message("Failed to update transfer")
                    .build();
        }
    }

    @Override
    public ApiResponse<TransferResponseDeleteAt> trashed(Long transferId) {
        log.info("🗑️ Trashing transfer id={}", transferId);

        try {
            Transfer transfer = transferCommandRepository.trashed(transferId);
            return ApiResponse.<TransferResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Transfer trashed successfully!")
                    .data(TransferResponseDeleteAt.from(transfer))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash transfer id={}", transferId, e);
            return ApiResponse.<TransferResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash transfer")
                    .build();

        }
    }

    @Override
    public ApiResponse<TransferResponseDeleteAt> restore(Long transferId) {
        log.info("♻️ Restoring transfer id={}", transferId);

        try {
            Transfer transfer = transferCommandRepository.restore(transferId);
            return ApiResponse.<TransferResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Transfer restored successfully!")
                    .data(TransferResponseDeleteAt.from(transfer))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore transfer id={}", transferId, e);
            return ApiResponse.<TransferResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore transfer")
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanent(Long transferId) {
        log.info("🧨 Permanently deleting transfer id={}", transferId);

        try {
            transferCommandRepository.deletePermanent(transferId);
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Transfer permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete transfer id={}", transferId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to permanently delete transfer")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed transfers");

        try {
            transferCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All transfers restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all transfers", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to restore all transfers")
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAll() {
        log.info("💣 Permanently deleting ALL trashed transfers");

        try {
            transferCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All transfers permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all transfers", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("💥 Failed to delete all transfers")
                    .data(false)
                    .build();
        }
    }

    private void rollbackSenderSaldo(Saldo sender, Long amountDifference) {
        try {
            sender.setTotalBalance(sender.getTotalBalance() + amountDifference);
            saldoCommandRepository.updateBalanceByCardNumber(sender.getCardNumber(), sender.getTotalBalance());
        } catch (Exception e) {
            log.error("⚠️ Rollback sender saldo failed: {}", e.getMessage());
        }
    }

    private void rollbackReceiverSaldo(Saldo receiver, Long amountDifference) {
        try {
            receiver.setTotalBalance(receiver.getTotalBalance() - amountDifference);
            saldoCommandRepository.updateBalanceByCardNumber(receiver.getCardNumber(), receiver.getTotalBalance());
        } catch (Exception e) {
            log.error("⚠️ Rollback receiver saldo failed: {}", e.getMessage());
        }
    }

}
