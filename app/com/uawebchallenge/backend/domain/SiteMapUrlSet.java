package com.uawebchallenge.backend.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created by Anatoliy Papenko on 3/29/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XStreamAlias("urlset")
@FieldDefaults(level = PRIVATE)
public class SiteMapUrlSet {
    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    final String xmlns = "http://www.sitemaps.org/schemas/sitemap/0.9";

    @XStreamImplicit(itemFieldName = "url")
    List<SiteMapUrl> urls;
}
