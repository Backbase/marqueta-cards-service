package com.backbase.productled.utils;

public enum FrequencyEnum {
    DAILY("DAILY", "DAY"),

    WEEKLY("WEEKLY", "WEEK"),

    MONTHLY("MONTHLY", "MONTH");

    private String dbsValue;
    private String marqetaValue;

    FrequencyEnum(String dbsValue, String marqetaValue) {
        this.dbsValue = dbsValue;
        this.marqetaValue = marqetaValue;
    }

    public String getValue() {
        return dbsValue;
    }

    public static FrequencyEnum fromValue(String value) {
        for (FrequencyEnum b : FrequencyEnum.values()) {
            if (b.marqetaValue.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}