package com.uawebchallenge.backend.converter;

import javax.persistence.AttributeConverter;
import java.time.ZonedDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;
import static java.util.Date.from;

public class DateConverter implements AttributeConverter<ZonedDateTime, Date> {
    @Override
    public Date convertToDatabaseColumn(ZonedDateTime attribute) {
        return from(attribute.toInstant().atZone(UTC).toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Date dbData) {
        return dbData.toInstant().atZone(UTC);
    }
}
