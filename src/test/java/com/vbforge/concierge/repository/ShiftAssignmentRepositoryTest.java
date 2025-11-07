package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.entity.ShiftAssignment;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.enums.ShiftType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for ShiftAssignment entity
 */
@DataJpaTest
@ActiveProfiles("test")
class ShiftAssignmentRepositoryTest {

    @Autowired
    private ShiftAssignmentRepository shiftRepository;

    @Autowired
    private ConciergeRepository conciergeRepository;

    private Concierge alice;
    private ShiftAssignment shift1;
    private ShiftAssignment shift2;

    @BeforeEach
    void setUp() {
        shiftRepository.deleteAll();
        conciergeRepository.deleteAll();

        alice = Concierge.builder()
                .name("Alice")
                .color(ColorType.BLUE)
                .active(true)
                .build();
        alice = conciergeRepository.save(alice);

        shift1 = ShiftAssignment.builder()
                .shiftDate(LocalDate.of(2025, 11, 1))
                .concierge(alice)
                .shiftType(ShiftType.FULL_DAY)
                .build();

        shift2 = ShiftAssignment.builder()
                .shiftDate(LocalDate.of(2025, 11, 2))
                .concierge(alice)
                .shiftType(ShiftType.FULL_DAY)
                .build();

        shiftRepository.saveAll(List.of(shift1, shift2));
    }

    @Test
    void shouldSaveShiftAssignment() {
        ShiftAssignment shift = ShiftAssignment.builder()
                .shiftDate(LocalDate.of(2025, 11, 3))
                .concierge(alice)
                .shiftType(ShiftType.FULL_DAY)
                .notes("Test shift")
                .build();

        ShiftAssignment saved = shiftRepository.save(shift);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getShiftDate()).isEqualTo(LocalDate.of(2025, 11, 3));
        assertThat(saved.getConcierge().getName()).isEqualTo("Alice");
        assertThat(saved.getNotes()).isEqualTo("Test shift");
    }

    @Test
    void shouldFindByShiftDateAndDeletedFalse() {
        Optional<ShiftAssignment> found = shiftRepository
                .findByShiftDateAndDeletedFalse(LocalDate.of(2025, 11, 1));

        assertThat(found).isPresent();
        assertThat(found.get().getConcierge().getName()).isEqualTo("Alice");
    }

    @Test
    void shouldFindByConciergeIdAndDeletedFalse() {
        List<ShiftAssignment> shifts = shiftRepository
                .findByConciergeIdAndDeletedFalse(alice.getId());

        assertThat(shifts).hasSize(2);
    }

    @Test
    void shouldFindByYearAndMonth() {
        List<ShiftAssignment> shifts = shiftRepository
                .findByYearAndMonth(2025, 11);

        assertThat(shifts).hasSize(2);
        assertThat(shifts.get(0).getShiftDate()).isBefore(shifts.get(1).getShiftDate());
    }

    @Test
    void shouldFindByShiftDateBetween() {
        LocalDate start = LocalDate.of(2025, 11, 1);
        LocalDate end = LocalDate.of(2025, 11, 30);

        List<ShiftAssignment> shifts = shiftRepository
                .findByShiftDateBetweenAndDeletedFalse(start, end);

        assertThat(shifts).hasSize(2);
    }

    @Test
    void shouldCheckIfShiftExists() {
        boolean exists = shiftRepository
                .existsByShiftDateAndDeletedFalse(LocalDate.of(2025, 11, 1));
        boolean notExists = shiftRepository
                .existsByShiftDateAndDeletedFalse(LocalDate.of(2025, 11, 10));

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldCountByYearAndMonth() {
        Long count = shiftRepository.countByYearAndMonth(2025, 11);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldFindByYear() {
        List<ShiftAssignment> shifts = shiftRepository.findByYear(2025);

        assertThat(shifts).hasSize(2);
    }

}