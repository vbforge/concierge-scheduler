package com.vbforge.concierge.entity;

import com.vbforge.concierge.enums.ColorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Concierge entity representing a staff member
 */
@Entity
@Table(name = "concierges",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Concierge extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Concierge name is required")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotNull(message = "Color is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false, columnDefinition = "varchar(20)")
    private ColorType color;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * One concierge can have many shift assignments
     * Cascade: when concierge is deleted, shifts are NOT automatically deleted (handled manually)
     */
    @OneToMany(mappedBy = "concierge", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShiftAssignment> shiftAssignments = new ArrayList<>();

    /**
     * Activate concierge
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivate concierge
     */
    public void deactivate() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "Concierge{id=" + id + ", name='" + name + "', color=" + color + ", active=" + active + "}";
    }

}
