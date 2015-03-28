package com.uawebchallenge.backend.service.impl;

import com.uawebchallenge.backend.repository.GeneratedSiteMapRepository;
import com.uawebchallenge.backend.service.ChangeFrequency;
import com.uawebchallenge.backend.service.SiteMapUrl;
import com.uawebchallenge.backend.service.SiteMapsService;
import lombok.experimental.FieldDefaults;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.libs.F.Promise;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static play.libs.ws.WS.url;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
@Service
@FieldDefaults(level = PRIVATE)
public class SiteMapsServiceImpl implements SiteMapsService {
    static final SiteMapUrl RESERVED = new SiteMapUrl();
    GeneratedSiteMapRepository generatedSiteMapRepository;
    UrlValidator urlValidator;

    @Autowired
    public SiteMapsServiceImpl(GeneratedSiteMapRepository generatedSiteMapRepository, UrlValidator urlValidator) {
        this.generatedSiteMapRepository = generatedSiteMapRepository;
        this.urlValidator = urlValidator;
    }

    @Override
    public Promise<List<SiteMapUrl>> generate(URL link, ChangeFrequency defaultChangeFrequency, Double defaultPriority, Integer depth) {
        final List<URL> reservedUrls = new LinkedList<>();
        final List<Promise<SiteMapUrl>> promises = new LinkedList<>();
        final List<SiteMapUrl> siteMapUrls = new LinkedList<>();
        return generate(reservedUrls, promises, link, depth)
                .flatMap(siteMapUrl -> {
                    siteMapUrls.add(siteMapUrl);
                    return Promise.sequence(promises);
                }).map(newSiteMapUrls -> {
                    siteMapUrls.addAll(newSiteMapUrls);
                    for (SiteMapUrl siteMapUrl : siteMapUrls) {
                        siteMapUrl.setChangeFrequency(defaultChangeFrequency);
                        siteMapUrl.setPriority(defaultPriority);
                    }
                    return siteMapUrls;
                });
    }

    private Promise<SiteMapUrl> generate(List<URL> reservedUrls, List<Promise<SiteMapUrl>> promises, URL link, Integer depth) {
        return url(link.toString()).get().map(response -> {
            final Document site = Jsoup.parse(response.getBody());
            final SiteMapUrl siteMapUrl = new SiteMapUrl();
            siteMapUrl.setLocation(link.toString());
            ofNullable(response.getHeader("Last-Modified")).ifPresent(siteMapUrl::setModifiedDate);
            //pLogger.error(siteMapUrl.toString());

            if (depth >= 0) {
                scanForLinks(link, site).stream()
                        .filter(uri -> !reservedUrls.contains(uri))
                        .forEach(uri -> {
                            reservedUrls.add(uri);
                            promises.add(generate(reservedUrls, promises, uri, depth - 1));
                        });
            }
            return siteMapUrl;
        });
    }

    private Set<URL> scanForLinks(URL uri, Document site) {
        return site.select("a[href]").stream()
                .map(link -> {
                    final String href = substringBeforeLast(link.attr("href"), "?");
                    return removeEnd(href, "/");
                })
                .filter(urlValidator::isValid)
                .map(link -> addDomainIfNeedAndReturnURI(link, uri))
                .filter(link -> link != null && link.getHost().equals(uri.getHost()))
                .collect(toSet());
    }

    private URL addDomainIfNeedAndReturnURI(String href, URL parentUri) {
        if (href.startsWith("/")) {
            href = parentUri.getProtocol() + "://" + parentUri.getHost() + href;
        }
        try {
            return new URL(href);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
