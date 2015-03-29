package com.uawebchallenge.backend.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.uawebchallenge.backend.service.ChangeFrequency;

/**
 * Created by Anatoliy Papenko on 3/29/15.
 */
public class ChangeFrequencyConverter implements Converter {
    @Override
    public boolean canConvert(Class type) {
        return type == ChangeFrequency.class;
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == null) {
            writer.setValue("null");
        }
        writer.setValue(((ChangeFrequency) source).name().toLowerCase());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final String value = reader.getValue();
        if (value == null) {
            return null;
        }
        if (!ChangeFrequency.hasValue(value)) {
            throw new RuntimeException("Change frequency doesn't contain " + value);
        }
        return null;
    }
}
