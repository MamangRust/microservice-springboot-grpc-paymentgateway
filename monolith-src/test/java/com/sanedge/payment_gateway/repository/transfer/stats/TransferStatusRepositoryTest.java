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
import com.sanedge.payment_gateway.models.transfer.TransferMonthStatusFailed;
import com.sanedge.payment_gateway.models.transfer.TransferMonthStatusSuccess;
import com.sanedge.payment_gateway.models.transfer.TransferYearStatusFailed;
import com.sanedge.payment_gateway.models.transfer.TransferYearStatusSuccess;
import com.sanedge.payment_gateway.repository.transfer.TransferCommandRepository;

public class TransferStatusRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferStatusRepository statusRepository;

    @Autowired
    private TransferCommandRepository commandRepository;

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        // Success t1
        Transfer t1 = new Transfer();
        t1.setTransferNo(UUID.randomUUID());
        t1.setTransferFrom("1111222233334444");
        t1.setTransferTo("5555666677778888");
        t1.setTransferAmount(500000L);
        t1.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        // Failed t2
        Transfer t2 = new Transfer();
        t2.setTransferNo(UUID.randomUUID());
        t2.setTransferFrom("1111222233334444");
        t2.setTransferTo("5555666677778888");
        t2.setTransferAmount(300000L);
        t2.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        t2.setStatus(Status.FAILED);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthTransferStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthStatusSuccess> results = statusRepository.findMonthTransferStatusSuccess(year, 1, year, 12);

        assertThat(results).isNotEmpty();

        TransferMonthStatusSuccess april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
        assertThat(april.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindMonthTransferStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthStatusFailed> results = statusRepository.findMonthTransferStatusFailed(year, 1, year, 12);

        assertThat(results).isNotEmpty();

        TransferMonthStatusFailed may = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(1L);
        assertThat(may.getTotalAmount()).isEqualTo(300000L);
    }

    @Test
    void testFindYearlyTransferStatusSuccess() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearStatusSuccess> results = statusRepository.findYearlyTransferStatusSuccess(year);

        assertThat(results).isNotEmpty();

        TransferYearStatusSuccess current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
        assertThat(current.getTotalAmount()).isEqualTo(500000L);
    }

    @Test
    void testFindYearlyTransferStatusFailed() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearStatusFailed> results = statusRepository.findYearlyTransferStatusFailed(year);

        assertThat(results).isNotEmpty();

        TransferYearStatusFailed current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(1L);
        assertThat(current.getTotalAmount()).isEqualTo(300000L);
    }
}