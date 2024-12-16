package com.stockfetcher.enums;

public enum Interval_Slice {

    ONE_MINUTE("1min"),
    FIVE_MINUTES("5min"),
    FIFTEEN_MINUTES("15min"),
    THIRTY_MINUTES("30min"),
    FORTY_FIVE_MINUTES("45min"),
    ONE_HOUR("1h"),
    TWO_HOURS("2h"),
    FOUR_HOURS("4h"),
    EIGHT_HOURS("8h"),
	ONE_DAY("1day"),
	ONE_WEEK("1week"),
	ONE_MONTH("1month");

    private final String value;

    Interval_Slice(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get the enum instance for a given string value.
     *
     * @param value the string representation of the interval
     * @return the corresponding Interval enum instance
     * @throws IllegalArgumentException if the value does not match any enum instance
     */
    public static Interval_Slice fromValue(String value) {
        for (Interval_Slice interval : Interval_Slice.values()) {
            if (interval.value.equals(value)) {
                return interval;
            }
        }
        throw new IllegalArgumentException("Invalid interval value: " + value);
    }
}
