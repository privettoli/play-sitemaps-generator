package com.uawebchallenge.backend.service.impl;

import com.google.common.collect.ImmutableSet;
import com.uawebchallenge.backend.domain.GeneratedSiteMapEntity;
import com.uawebchallenge.backend.domain.SiteMapUrl;
import com.uawebchallenge.backend.exception.BadLinkException;
import com.uawebchallenge.backend.repository.GeneratedSiteMapRepository;
import com.uawebchallenge.backend.service.ChangeFrequency;
import com.uawebchallenge.backend.service.SiteMapService;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import scala.concurrent.ExecutionContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.String.format;
import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.*;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.*;
import static play.libs.F.Promise.promise;
import static play.mvc.Http.Status.*;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
@Service
@FieldDefaults(level = PRIVATE)
public class SiteMapServiceImpl implements SiteMapService {
    static final Pair<List<SiteMapUrl>, Set<URL>> EXCEPTION_OCCURS = new ImmutablePair<>(emptyList(), emptySet());
    static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
    GeneratedSiteMapRepository generatedSiteMapRepository;
    ExecutionContext databaseExecutionContext;
    UrlValidator urlValidator;
    WSClient ws;

    @Autowired
    public SiteMapServiceImpl(GeneratedSiteMapRepository generatedSiteMapRepository,
                              @Qualifier("databaseExecutionContext") ExecutionContext databaseExecutionContext,
                              UrlValidator urlValidator, WSClient ws) {
        this.ws = ws;
        this.urlValidator = urlValidator;
        this.generatedSiteMapRepository = generatedSiteMapRepository;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public Promise<Optional<GeneratedSiteMapEntity>> findBy(Long id) {
        return promise(() -> ofNullable(generatedSiteMapRepository.findOne(id)), databaseExecutionContext);
    }

    @Override
    public Promise<Boolean> save(GeneratedSiteMapEntity generatedSiteMapEntity) {
        return promise(() -> {
            try {
                generatedSiteMapRepository.save(generatedSiteMapEntity);
                return true;
            } catch (RuntimeException e) {
                Logger.error(e.getMessage(), e);
                return false;
            }
        }, databaseExecutionContext);
    }

    @Override
    public Promise<List<SiteMapUrl>> generate(URL link, ChangeFrequency defaultChangeFrequency, Double defaultPriority, Integer depth) {
        final List<URL> reservedUrls = new LinkedList<>();
        Promise<Pair<List<SiteMapUrl>, Set<URL>>> promise = generate(reservedUrls, link);
        for (int i = 0; i < depth; i++) {
            promise = promise.flatMap(oldPair -> {
                final List<Promise<Pair<List<SiteMapUrl>, Set<URL>>>> results =
                        oldPair.getValue().stream()
                                .map(url -> generate(reservedUrls, url))
                                .collect(toList());
                return Promise.sequence(results).map(list -> {
                    Pair<List<SiteMapUrl>, Set<URL>> previousResult = new ImmutablePair<>(new ArrayList<>(), new HashSet<>());
                    previousResult.getKey().addAll(oldPair.getKey());
                    for (Pair<List<SiteMapUrl>, Set<URL>> parsingResult : list) {
                        if (parsingResult.equals(EXCEPTION_OCCURS)) {
                            continue;
                        }
                        previousResult.getKey().addAll(parsingResult.getKey());
                        previousResult.getValue().addAll(parsingResult.getValue());
                    }
                    return previousResult;
                });
            });
        }
        return promise.map(newSiteMapUrls -> newSiteMapUrls.getKey().parallelStream().map(siteMapUrl -> {
                    siteMapUrl.setChangeFrequency(defaultChangeFrequency);
                    siteMapUrl.setPriority(defaultPriority);
                    return siteMapUrl;
                }).collect(toList())
        );
    }

    @Override
    public byte[] packageToZip(GeneratedSiteMapEntity generatedSiteMapEntity) throws IOException {
        final byte[] result;
        // If you init OutputSteam inside ZipOutputStream's constructor, then it will not be closed properly
        try (
                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)
        ) {
            List<byte[]> urlSets = generatedSiteMapEntity.getUrlSets();
            for (int i = 0; i < urlSets.size(); i++) {
                byte[] urlSet = urlSets.get(i);
                final ZipEntry zipEntry = new ZipEntry(format("sitemap (%d).xml", i + 1));
                zipEntry.setSize(urlSet.length);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(urlSet);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            result = outputStream.toByteArray();
        }
        return result;
    }

    private Promise<Pair<List<SiteMapUrl>, Set<URL>>> generate(List<URL> reservedUrls, URL link) {
        return ws.url(link.toString()).get().recover(throwable -> null).map(response -> {
            if (response == null) {
                throw new BadLinkException();
            }
            final int status = response.getStatus();
            if (status == FOUND || status == SEE_OTHER) {
                return getRedirectLinkOrThrowException(link, response);
            }
            if (status != OK) {
                throw new BadLinkException();
            }
            if (!contains(response.getHeader("Content-Type"), "text/html")) {
                throw new BadLinkException();
            }
            final Document site = Jsoup.parse(response.getBody());
            final SiteMapUrl siteMapUrl = new SiteMapUrl();
            siteMapUrl.setLocation(link.toString());
            ofNullable(response.getHeader("Last-Modified")).ifPresent(stringDate -> {
                siteMapUrl.setModifiedDate(parse(stringDate, DATE_TIME_FORMATTER));
            });

            Set<URL> urls = scanSiteForLinksUnderSameDomain(site, link);
            synchronized (reservedUrls) {
                urls = urls.stream().filter(uri -> !reservedUrls.contains(uri))
                        .map(uri -> {
                            reservedUrls.add(uri);
                            return uri;
                        }).collect(toSet());
            }
            return (Pair<List<SiteMapUrl>, Set<URL>>) new ImmutablePair<>(singletonList(siteMapUrl), urls);
        }).recover(throwable -> EXCEPTION_OCCURS);
    }

    private Pair<List<SiteMapUrl>, Set<URL>> getRedirectLinkOrThrowException(URL link, WSResponse response) {
        final String location = response.getHeader("Location");
        if (isBlank(location)) {
            throw new BadLinkException();
        }
        final URL url = clarifyAndReturnUrl(prepareLink(location), link);
        if (isUnderSameDomain(link, url)) {
            return new ImmutablePair<List<SiteMapUrl>, Set<URL>>(emptyList(), ImmutableSet.of(url));
        } else {
            throw new BadLinkException();
        }
    }

    private Set<URL> scanSiteForLinksUnderSameDomain(Document site, URL domainUrl) {
        return site.select("a[href]").stream()
                .map(link -> link.attr("href"))
                .filter(StringUtils::isNotBlank)
                .map(this::prepareLink)
                .map(link -> clarifyAndReturnUrl(link, domainUrl))
                .filter(link -> isUnderSameDomain(domainUrl, link))
                .collect(toSet());
    }

    private boolean isUnderSameDomain(URL domainUrl, URL link) {
        return link != null && link.getHost().equals(domainUrl.getHost());
    }

    private String prepareLink(String link) {
        String href = substringBeforeLast(link, "#");
        return removeEnd(href, "/");
    }

    private URL clarifyAndReturnUrl(String href, URL parentUri) {
        if (href.startsWith("/")) {
            href = parentUri.getProtocol() + "://" + parentUri.getHost() + href;
        }
        if (!urlValidator.isValid(href)) {
            return null;
        }
        try {
            return new URL(href);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
