package com.vbforge.concierge.repository;

import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.enums.ColorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for Concierge entity
 * Uses H2 in-memory database
 */
@DataJpaTest
@ActiveProfiles("test")
class ConciergeRepositoryTest {

    @Autowired
    private ConciergeRepository conciergeRepository;

    private Concierge alice;
    private Concierge bob;

    @BeforeEach
    void setUp() {
        conciergeRepository.deleteAll();

        alice = Concierge.builder()
                .name("Alice")
                .color(ColorType.BLUE)
                .active(true)
                .build();

        bob = Concierge.builder()
                .name("Bob")
                .color(ColorType.PURPLE)
                .active(false)
                .build();

        conciergeRepository.saveAll(List.of(alice, bob));
    }

    @Test
    void shouldSaveConcierge() {
        Concierge carol = Concierge.builder()
                .name("Carol")
                .color(ColorType.GREEN)
                .active(true)
                .build();

        Concierge saved = conciergeRepository.save(carol);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Carol");
        assertThat(saved.getColor()).isEqualTo(ColorType.GREEN);
        assertThat(saved.isActive()).isTrue();
    }

    @Test
    void shouldFindByNameIgnoreCase() {
        Optional<Concierge> found = conciergeRepository.findByNameIgnoreCase("alice");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Alice");
    }

    @Test
    void shouldFindActiveConciergesTrue() {
        List<Concierge> active = conciergeRepository.findByActiveTrue();

        assertThat(active).hasSize(1);
        assertThat(active.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void shouldFindByColor() {
        List<Concierge> blueOnes = conciergeRepository.findByColor(ColorType.BLUE);

        assertThat(blueOnes).hasSize(1);
        assertThat(blueOnes.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void shouldCheckIfNameExists() {
        boolean exists = conciergeRepository.existsByNameIgnoreCase("Alice");
        boolean notExists = conciergeRepository.existsByNameIgnoreCase("David");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldFindActiveTrueAndDeletedFalse() {
        List<Concierge> result = conciergeRepository.findByActiveTrueAndDeletedFalse();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void shouldSoftDelete() {
        alice.softDelete();
        conciergeRepository.save(alice);

        List<Concierge> nonDeleted = conciergeRepository.findByDeletedFalse();

        assertThat(nonDeleted).hasSize(1);
        assertThat(nonDeleted.get(0).getName()).isEqualTo("Bob");
    }

}