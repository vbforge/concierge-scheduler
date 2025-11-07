package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.MonthHistory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Repository tests for MonthHistory entity
 */
@DataJpaTest
@ActiveProfiles("test")
class MonthHistoryRepositoryTest {

    @Autowired
    private MonthHistoryRepository historyRepository;

    @BeforeEach
    void setUp() {

        MonthHistory history1 = MonthHistory.builder()
                .year(2025)
                .month(10)
                .snapshotJson("{\"shifts\":[]}")
                .snapshotDate(LocalDateTime.now().minusMonths(1))
                .totalShifts(15)
                .description("October snapshot")
                .build();

        MonthHistory history2 = MonthHistory.builder()
                .year(2025)
                .month(9)
                .snapshotJson("{\"shifts\":[]}")
                .snapshotDate(LocalDateTime.now().minusMonths(2))
                .totalShifts(20)
                .description("September snapshot")
                .build();

        historyRepository.saveAll(List.of(history1, history2));
    }

    @AfterEach
    public void cleanUp(){
        historyRepository.deleteAll();
    }

    @Test
    void shouldSaveMonthHistory() {
        MonthHistory history = MonthHistory.builder()
                .year(2025)
                .month(11)
                .snapshotJson("{\"shifts\":[]}")
                .snapshotDate(LocalDateTime.now())
                .totalShifts(25)
                .build();

        MonthHistory saved = historyRepository.save(history);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getYear()).isEqualTo(2025);
        assertThat(saved.getMonth()).isEqualTo(11);
        assertThat(saved.getTotalShifts()).isEqualTo(25);
    }

    @Test
    void shouldFindByYearAndMonth() {
        Optional<MonthHistory> found = historyRepository
                .findByYearAndMonthAndDeletedFalse(2025, 10);

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("October snapshot");
    }

    @Test
    void shouldFindByYearOrderedByMonth() {
        List<MonthHistory> histories = historyRepository
                .findByYearAndDeletedFalseOrderByMonthDesc(2025);

        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getMonth()).isEqualTo(10); // Most recent first
        assertThat(histories.get(1).getMonth()).isEqualTo(9);
    }

    @Test
    void shouldFindAllOrderedByDate() {
        List<MonthHistory> histories = historyRepository
                .findByDeletedFalseOrderByYearDescMonthDesc();

        assertThat(histories).hasSize(2);
        assertThat(histories.get(0).getMonth()).isEqualTo(10);
    }

    @Test
    void shouldFindWithPagination() {
        Page<MonthHistory> page = historyRepository
                .findByDeletedFalse(PageRequest.of(0, 1));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldCheckIfExists() {
        boolean exists = historyRepository
                .existsByYearAndMonthAndDeletedFalse(2025, 10);
        boolean notExists = historyRepository
                .existsByYearAndMonthAndDeletedFalse(2025, 11);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldCountNonDeleted() {
        long count = historyRepository.countByDeletedFalse();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldFindByYearRange() {
        List<MonthHistory> histories = historyRepository
                .findByYearRange(2025, 2025);

        assertThat(histories).hasSize(2);
    }
}