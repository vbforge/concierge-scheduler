package com.vbforge.concierge.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * MonthHistory entity storing monthly schedule snapshots in JSON format
 */
@Entity
@Table(name = "month_history",
        uniqueConstraints = @UniqueConstraint(columnNames = {"year_value", "month_value"}),
        indexes = {
                @Index(name = "idx_year_month", columnList = "year_value, month_value")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthHistory extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Year is required")
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    @Column(name = "year_value", nullable = false)
    private Integer year;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(name = "month_value", nullable = false)
    private Integer month;

    /**
     * JSON snapshot of the entire month's schedule
     * Stores serialized list of ShiftAssignmentDto objects
     */
    @NotNull(message = "Snapshot JSON is required")
    @Column(name = "snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String snapshotJson;

    /**
     * Timestamp when snapshot was created
     */
    @NotNull
    @Column(name = "snapshot_date", nullable = false)
    @Builder.Default
    private LocalDateTime snapshotDate = LocalDateTime.now();

    /**
     * Optional description or reason for snapshot
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Number of shift assignments in this snapshot
     */
    @Column(name = "total_shifts")
    private Integer totalShifts;

    @Override
    public String toString() {
        return "MonthHistory{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", snapshotDate=" + snapshotDate +
                ", totalShifts=" + totalShifts +
                '}';
    }

}
