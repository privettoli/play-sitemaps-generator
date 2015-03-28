package com.uawebchallenge.backend.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
@XmlEnum
public enum ChangeFrequency {
    @XmlEnumValue("always")
    ALWAYS,

    @XmlEnumValue("hourly")
    HOURLY,

    @XmlEnumValue("daily")
    DAILY,

    @XmlEnumValue("weekly")
    WEEKLY,

    @XmlEnumValue("monthly")
    MONTHLY,

    @XmlEnumValue("yearly")
    YEARLY,

    @XmlEnumValue("never")
    NEVER;

    public static ChangeFrequency find(String name) {
        for (ChangeFrequency changeFrequency : values()) {
            if (changeFrequency.name().toLowerCase().equals(name.toLowerCase())) {
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
            if (changeFrequency.name().toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
