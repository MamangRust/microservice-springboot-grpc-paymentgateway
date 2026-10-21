package com.sanedge.payment_gateway.repository.transfer;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;

public class TransferQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferQueryRepository queryRepository;

    @Autowired
    private TransferCommandRepository commandRepository;

    private Long transferId;
    private String transferFrom = "1111222233334444";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom(transferFrom);
        transfer.setTransferTo("5555666677778888");
        transfer.setTransferAmount(500000L);
        transfer.setTransferTime(new Timestamp(System.currentTimeMillis()));
        transfer.setStatus(Status.PENDING);

        Transfer saved = commandRepository.save(transfer);
        transferId = saved.getTransferId();
    }

    @Test
    void testFindTransfers() {
        Page<Transfer> page = queryRepository.findTransfers("1111", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindActiveTransfers() {
        Page<Transfer> page = queryRepository.findActiveTransfers(null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindTrashedTransfers() {
        commandRepository.trashed(transferId);
        Page<Transfer> page = queryRepository.findTrashedTransfers(null, PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindTransferById() {
        java.util.Optional<Transfer> transfer = queryRepository.findTransferById(transferId);
        assertThat(transfer).isPresent();
        assertThat(transfer.get().getTransferId()).isEqualTo(transferId);
    }

    @Test
    void testFindTransfersByCardNumber() {
        java.util.List<Transfer> page = queryRepository.findTransfersByCardNumber(transferFrom);
        assertThat(page).isNotEmpty();
    }
}