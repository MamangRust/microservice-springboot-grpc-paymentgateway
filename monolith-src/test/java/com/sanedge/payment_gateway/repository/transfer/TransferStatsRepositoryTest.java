package com.sanedge.payment_gateway.repository.transfer;

        import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.models.transfer.TransferMonthAmount;
import com.sanedge.payment_gateway.repository.transfer.stats.TransferAmountRepository;

public class TransferStatsRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferAmountRepository transferAmountRepository;

    @Autowired
    private TransferCommandRepository transferCommandRepository;

    @BeforeEach
    void setUp() {
        transferCommandRepository.deleteAll();

        Transfer transfer = new Transfer();
        transfer.setTransferNo(UUID.randomUUID());
        transfer.setTransferFrom("1111222233334444");
        transfer.setTransferTo("5555666677778888");
        transfer.setTransferAmount(150L);
        transfer.setTransferTime(new Timestamp(System.currentTimeMillis()));
        transfer.setStatus(Status.SUCCESS);
        transferCommandRepository.save(transfer);
    }

    @Test
    void shouldGetMonthlyTransferAmounts() {
        Long currentYear = (long) LocalDate.now().getYear();
        List<TransferMonthAmount> stats = transferAmountRepository.findMonthlyTransferAmounts(currentYear);
        assertThat(stats).isNotEmpty();
        assertThat(stats.stream().anyMatch(s -> s.getTotalAmount() > 0)).isTrue();
    }
}