package com.vbforge.concierge.enums;

/**
 * Shift types - currently only FULL_DAY (24 hours)
 * Can be extended in future for MORNING, EVENING, NIGHT shifts
 */
public enum ShiftType {

    FULL_DAY("24 Hours", "Full Day Shift");

    private final String duration;
    private final String description;

    ShiftType(String duration, String description) {
        this.duration = duration;
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }
}