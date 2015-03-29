package com.uawebchallenge.backend.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by Anatoliy Papenko on 3/29/15.
 *
 * @see <a href="http://www.w3.org/TR/NOTE-datetime">http://www.w3.org/TR/NOTE-datetime</a>
 */
@FieldDefaults(level = PRIVATE)
public class DateTimeToISOConverter implements Converter {
    static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("YYYY-MM-dd'T'HH:mm:ssxxx");

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == null) {
            return;
        }
        final ZonedDateTime date = (ZonedDateTime) source;
        writer.setValue(date.format(DATE_TIME_FORMATTER));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final String date = reader.getValue();
        if (isBlank(date)) {
            return null;
        }
        return ZonedDateTime.parse(date, DATE_TIME_FORMATTER);
    }

    @Override
    public boolean canConvert(Class type) {
        return type == ZonedDateTime.class;
    }
}
