package com.sanedge.payment_gateway.repository.transfer.statsbycard;

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

public class TransferStatusByCardRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TransferStatusByCardRepository statusByCardRepository;

    @Autowired
    private TransferCommandRepository commandRepository;

    private String cardNumber = "1111222233334444";

    @BeforeEach
    void setUp() {
        commandRepository.deleteAll();

        Long currentYear = (long) LocalDateTime.now().getYear();

        // Success t1 (cardNumber as sender)
        Transfer t1 = new Transfer();
        t1.setTransferNo(UUID.randomUUID());
        t1.setTransferFrom(cardNumber);
        t1.setTransferTo("5555666677778888");
        t1.setTransferAmount(500000L);
        t1.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 4, 15, 10, 0)));
        t1.setStatus(Status.SUCCESS);
        commandRepository.save(t1);

        // Failed t2 (different card)
        Transfer t2 = new Transfer();
        t2.setTransferNo(UUID.randomUUID());
        t2.setTransferFrom("6543210987654321");
        t2.setTransferTo("5555666677778888");
        t2.setTransferAmount(300000L);
        t2.setTransferTime(Timestamp.valueOf(LocalDateTime.of(currentYear.intValue(), 5, 20, 14, 0)));
        t2.setStatus(Status.FAILED);
        commandRepository.save(t2);
    }

    @Test
    void testFindMonthTransferStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthStatusSuccess> results = statusByCardRepository.findMonthTransferStatusSuccessByCard(cardNumber, year, 1, year, 12);

        assertThat(results).isNotEmpty();

        TransferMonthStatusSuccess april = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("Apr"))
                .findFirst()
                .orElseThrow();
        assertThat(april.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindMonthTransferStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferMonthStatusFailed> results = statusByCardRepository.findMonthTransferStatusFailedByCard(cardNumber, year, 1, year, 12);

        assertThat(results).isNotEmpty();

        TransferMonthStatusFailed may = results.stream()
                .filter(r -> r.getMonthName().equalsIgnoreCase("May"))
                .findFirst()
                .orElseThrow();
        assertThat(may.getTotalFailed()).isEqualTo(0L); // Should be 0 for this card;
    }

    @Test
    void testFindYearlyTransferStatusSuccessByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearStatusSuccess> results = statusByCardRepository.findYearlyTransferStatusSuccessByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        TransferYearStatusSuccess current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalSuccess()).isEqualTo(1L);
    }

    @Test
    void testFindYearlyTransferStatusFailedByCard() {
        Long year = (long) LocalDateTime.now().getYear();
        List<TransferYearStatusFailed> results = statusByCardRepository.findYearlyTransferStatusFailedByCard(cardNumber, year);

        assertThat(results).isNotEmpty();

        TransferYearStatusFailed current = results.stream()
                .filter(r -> r.getReportYear().equals(String.valueOf(year)))
                .findFirst()
                .orElseThrow();
        assertThat(current.getTotalFailed()).isEqualTo(0L);
    }
}