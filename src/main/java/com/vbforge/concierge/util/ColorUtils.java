package com.vbforge.concierge.util;

import com.vbforge.concierge.enums.ColorType;

/**
 * Utility class for color operations
 */
public class ColorUtils {

    private ColorUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get RGB values from hex color code
     */
    public static int[] hexToRgb(String hexCode) {
        hexCode = hexCode.replace("#", "");
        return new int[]{
                Integer.parseInt(hexCode.substring(0, 2), 16),
                Integer.parseInt(hexCode.substring(2, 4), 16),
                Integer.parseInt(hexCode.substring(4, 6), 16)
        };
    }

    /**
     * Get CSS class name from color type
     */
    public static String getCssClassName(ColorType color) {
        return color.name().toLowerCase().replace("_", "-");
    }

    /**
     * Get lighter version of hex color (for backgrounds)
     */
    public static String getLighterColor(String hexCode, double factor) {
        int[] rgb = hexToRgb(hexCode);
        int r = Math.min(255, (int) (rgb[0] + (255 - rgb[0]) * factor));
        int g = Math.min(255, (int) (rgb[1] + (255 - rgb[1]) * factor));
        int b = Math.min(255, (int) (rgb[2] + (255 - rgb[2]) * factor));
        return String.format("#%02x%02x%02x", r, g, b);
    }

    /**
     * Get RGBA color string for CSS (with opacity)
     */
    public static String getRgbaColor(String hexCode, double opacity) {
        int[] rgb = hexToRgb(hexCode);
        return String.format("rgba(%d, %d, %d, %.2f)", rgb[0], rgb[1], rgb[2], opacity);
    }

    /**
     * Check if color is dark (for text color selection)
     */
    public static boolean isDarkColor(String hexCode) {
        int[] rgb = hexToRgb(hexCode);
        // Calculate luminance
        double luminance = (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]) / 255;
        return luminance < 0.5;
    }

    /**
     * Get contrasting text color (black or white) for background
     */
    public static String getContrastingTextColor(String hexCode) {
        return isDarkColor(hexCode) ? "#FFFFFF" : "#000000";
    }

    /**
     * Get all available colors for dropdown
     */
    public static ColorType[] getAllColors() {
        return ColorType.values();
    }
}