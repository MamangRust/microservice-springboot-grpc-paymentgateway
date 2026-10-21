package com.sanedge.payment_gateway.repository.transfer.stats;

        import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.transfer.Transfer;
import com.sanedge.payment_gateway.models.transfer.TransferMonthAmount;
import com.sanedge.payment_gateway.models.transfer.TransferYearAmount;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferAmountRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferAmountRepository amountRepository;

    @Autowired
    private TransferCommandRepository commandRepository;

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        Transfer t1 = new Transfer();
        t1.setTransferNo(UUID.randomUUID());
        t1.setTransferFrom("1111222233334444");
        t1.setTransferTo("5555666677778888");
        t1.setTransferAmount(500000L);
        t1.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0))); // April
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        Transfer t2 = new Transfer();
        t2.setTransferNo(UUID.randomUUID());
        t2.setTransferFrom("1111222233334444");
        t2.setTransferTo("5555666677778888");
        t2.setTransferAmount(300000L);
        t2.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0))); // May
        t2.setStatus(Status.SUCCESS);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthlyTransferAmounts() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthAmount> results = amountRepository.findMonthlyTransferAmounts(year);

        assertThat(results).hasSize(12);

        TransferMonthAmount april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalAmount()).isEqualTo(500000L);

        TransferMonthAmount may = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalAmount()).isEqualTo(300000L);
    }

    @Test
    void testFindYearlyTransferAmounts() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearAmount> results = amountRepository.findYearlyTransferAmounts(year);

        assertThat(results).isNotEmpty();

        TransferYearAmount current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalAmount()).isEqualTo(800000L);
    }
}