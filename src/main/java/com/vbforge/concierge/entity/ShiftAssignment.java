package com.vbforge.concierge.entity;

import com.vbforge.concierge.enums.ShiftType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * ShiftAssignment entity representing a work shift for a specific date
 */
@Entity
@Table(name = "shift_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"shift_date", "concierge_id"}),
        indexes = {
                @Index(name = "idx_shift_date", columnList = "shift_date"),
                @Index(name = "idx_concierge_id", columnList = "concierge_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftAssignment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Shift date is required")
    @Column(name = "shift_date", nullable = false)
    private LocalDate shiftDate;

    @NotNull(message = "Concierge is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concierge_id", nullable = false, foreignKey = @ForeignKey(name = "fk_shift_concierge"))
    private Concierge concierge;

    @NotNull(message = "Shift type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false, length = 20)
    @Builder.Default
    private ShiftType shiftType = ShiftType.FULL_DAY;

    /**
     * Notes or comments about this shift (optional)
     */
    @Column(name = "notes", length = 500)
    private String notes;

    @Override
    public String toString() {
        return "ShiftAssignment{" +
                "id=" + id +
                ", shiftDate=" + shiftDate +
                ", concierge=" + (concierge != null ? concierge.getName() : "null") +
                ", shiftType=" + shiftType +
                '}';
    }

}
