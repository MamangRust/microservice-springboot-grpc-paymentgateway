package com.sanedge.payment_gateway.repository.topup;

        import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.payment_gateway.BaseIntegrationTest;
import com.sanedge.payment_gateway.enums.Status;
import com.sanedge.payment_gateway.models.topup.Topup;

public class TopupQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private TopupQueryRepository topupQueryRepository;

    @Autowired
    private TopupCommandRepository topupCommandRepository;

    @BeforeEach
    void setUp() {
        topupCommandRepository.deleteAll();

        Topup topup1 = new Topup();
        topup1.setTopupNo(UUID.randomUUID());
        topup1.setCardNumber("1234567890");
        topup1.setTopupAmount(100000L);
        topup1.setTopupMethod("VA_BCA");
        topup1.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup1.setStatus(Status.PENDING);
        topupCommandRepository.save(topup1);

        Topup topup2 = new Topup();
        topup2.setTopupNo(UUID.randomUUID());
        topup2.setCardNumber("0987654321");
        topup2.setTopupAmount(200000L);
        topup2.setTopupMethod("VA_BNI");
        topup2.setTopupTime(new Timestamp(System.currentTimeMillis()));
        topup2.setStatus(Status.SUCCESS);
        topupCommandRepository.save(topup2);
    }

    @Test
    void shouldFindTopups() {
        Page<Topup> topups = topupQueryRepository.findTopups("BCA", PageRequest.of(0, 10));
        assertThat(topups.getContent()).hasSize(1);
        assertThat(topups.getContent().get(0).getCardNumber()).isEqualTo("1234567890");
    }

    @Test
    void shouldFindTopupByCard() {
        Page<Topup> topups = topupQueryRepository.findTopupByCard("0987654321", null, PageRequest.of(0, 10));
        assertThat(topups.getContent()).hasSize(1);
        assertThat(topups.getContent().get(0).getStatus()).isEqualTo(Status.SUCCESS);
    }

    @Test
    void shouldFindActiveTopups() {
        Page<Topup> topups = topupQueryRepository.findActiveTopups(null, PageRequest.of(0, 10));
        assertThat(topups.getContent()).hasSize(2);
    }

    @Test
    void shouldFindTrashedTopups() {
        Topup topup = topupQueryRepository.findAll().get(0);
        topupCommandRepository.trashed(topup.getTopupId());

        Page<Topup> topups = topupQueryRepository.findTrashedTopups(null, PageRequest.of(0, 10));
        assertThat(topups.getContent()).hasSize(1);
    }

    @Test
    void shouldFindByCardNumber() {
        List<Topup> topups = topupQueryRepository.findByCardNumber("1234567890");
        assertThat(topups).hasSize(1);
    }

    @Test
    void shouldFindTopupById() {
        Topup topup = topupQueryRepository.findAll().get(0);
        Topup found = topupQueryRepository.findTopupById(topup.getTopupId());
        assertThat(found).isNotNull();
        assertThat(found.getTopupId()).isEqualTo(topup.getTopupId());
    }
}