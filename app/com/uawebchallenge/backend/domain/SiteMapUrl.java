package com.uawebchallenge.backend.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.uawebchallenge.backend.converter.ChangeFrequencyConverter;
import com.uawebchallenge.backend.converter.DateTimeToISOConverter;
import com.uawebchallenge.backend.service.ChangeFrequency;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.Date;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class SiteMapUrl {
    @XStreamAlias("loc")
    String location;

    @XStreamAlias("lastmod")
    @XStreamConverter(DateTimeToISOConverter.class)
    ZonedDateTime modifiedDate;

    @XStreamAlias("changefreq")
    @XStreamConverter(ChangeFrequencyConverter.class)
    ChangeFrequency changeFrequency;

    Double priority;
}
