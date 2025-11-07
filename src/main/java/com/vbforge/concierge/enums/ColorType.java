package com.vbforge.concierge.enums;

/**
 * Color types for concierge identification in the calendar
 */
public enum ColorType {

    BLUE("#3498db", "Blue"),
    PURPLE("#9b59b6", "Purple"),
    GREEN("#2ecc71", "Green"),
    RED("#e74c3c", "Red"),
    ORANGE("#e67e22", "Orange"),
    YELLOW("#f39c12", "Yellow"),
    PINK("#e91e63", "Pink"),
    TEAL("#1abc9c", "Teal"),
    INDIGO("#6610f2", "Indigo"),
    CYAN("#17a2b8", "Cyan");

    private final String hexCode;
    private final String displayName;

    ColorType(String hexCode, String displayName) {
        this.hexCode = hexCode;
        this.displayName = displayName;
    }

    public String getHexCode() {
        return hexCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get ColorType by hex code
     */
    public static ColorType fromHexCode(String hexCode) {
        for (ColorType color : values()) {
            if (color.hexCode.equalsIgnoreCase(hexCode)) {
                return color;
            }
        }
        return null;
    }


}
