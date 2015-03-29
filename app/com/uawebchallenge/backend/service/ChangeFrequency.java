package com.uawebchallenge.backend.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
public enum ChangeFrequency {
    ALWAYS,
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    NEVER;

    public static ChangeFrequency find(String name) {
        for (ChangeFrequency changeFrequency : values()) {
            if (changeFrequency.name().equalsIgnoreCase(name)) {
                return changeFrequency;
            }
        }
        throw new IllegalArgumentException("No " + ChangeFrequency.class.getName() + " with such name");
    }

    public static boolean hasValue(String name) {
        if (isBlank(name)) {
            return false;
        }
        for (ChangeFrequency changeFrequency : values()) {
            if (changeFrequency.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
