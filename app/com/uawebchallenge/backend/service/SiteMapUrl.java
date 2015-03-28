package com.uawebchallenge.backend.service;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static lombok.AccessLevel.PRIVATE;

@Data
@XmlRootElement(name = "url")
@FieldDefaults(level = PRIVATE)
public class SiteMapUrl {
    @XmlAttribute(name = "loc")
    String location;

    /**
     * TODO add converter according to http://www.w3.org/TR/NOTE-datetime
     */
    @XmlAttribute(name = "lastmod")
    String modifiedDate;

    @XmlAttribute(name = "changefreq")
    ChangeFrequency changeFrequency;

    Double priority;
}
