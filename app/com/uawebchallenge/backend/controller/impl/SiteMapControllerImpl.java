package com.uawebchallenge.backend.controller.impl;

import com.thoughtworks.xstream.XStream;
import com.uawebchallenge.backend.controller.SiteMapController;
import com.uawebchallenge.backend.converter.GeneratedSiteMapConverter;
import com.uawebchallenge.backend.domain.GeneratedSiteMapEntity;
import com.uawebchallenge.backend.domain.UrlSetData;
import com.uawebchallenge.backend.domain.SiteMapUrlSet;
import com.uawebchallenge.backend.service.ChangeFrequency;
import com.uawebchallenge.backend.service.SiteMapService;
import lombok.experimental.FieldDefaults;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.validation.Constraints.EmailValidator;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.uawebchallenge.backend.service.ChangeFrequency.hasValue;
import static java.lang.Math.min;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.validator.GenericValidator.isDouble;
import static play.libs.F.Promise.pure;
import static play.libs.Json.toJson;

/**
 * Created by Anatoliy Papenko on 3/28/15.
 */
@FieldDefaults(level = PRIVATE)
@org.springframework.stereotype.Controller
public class SiteMapControllerImpl extends Controller implements SiteMapController {
    public static final int SITE_MAP_LIMIT = 50_000;
    SiteMapService siteMapService;
    UrlValidator urlValidator;
    EmailValidator emailValidator;
    XStream xStream;
    GeneratedSiteMapConverter generatedSiteMapConverter;

    @Autowired
    public SiteMapControllerImpl(SiteMapService siteMapService, UrlValidator urlValidator,
                                 EmailValidator emailValidator, XStream xStream,
                                 GeneratedSiteMapConverter generatedSiteMapConverter) {
        this.siteMapService = siteMapService;
        this.urlValidator = urlValidator;
        this.emailValidator = emailValidator;
        this.xStream = xStream;
        this.generatedSiteMapConverter = generatedSiteMapConverter;
    }

    @Override
    public Promise<Result> getAsZip(Long id) {
        return siteMapService.findBy(id).map((optionalSiteMap) -> {
                    if (!optionalSiteMap.isPresent()) {
                        return notFound("No generated site map with such id");
                    }
                    final GeneratedSiteMapEntity siteMapEntity = optionalSiteMap.get();
                    return ok(siteMapService.packageToZip(siteMapEntity));
                }
        );
    }

    @Override
    public Promise<Result> generate(String incomingUrl, final String sendToEmail, String changeFrequencyString,
                                    String priorityString, String depthString) {
        if (isBlank(incomingUrl)) {
            return pure(badRequest("Url should be not blank"));
        }
        incomingUrl = addHttpIfNeed(removeEnd(incomingUrl, "/"));
        if (!urlValidator.isValid(incomingUrl)) {
            return pure(badRequest("Url is not valid"));
        }
        final URL url;
        try {
            url = new URI(incomingUrl).toURL();
        } catch (Exception e) {
            return pure(badRequest("Can't extract host from url, please correct it"));
        }
        if (!isDefaultPort(url.getPort())) {
            return pure(badRequest("Your website should be on 80 port"));
        }
        if (!emailValidator.isValid(sendToEmail)) {
            return pure(badRequest("Email is not valid"));
        }
        if (!hasValue(changeFrequencyString)) {
            return pure(badRequest("Default change frequency is not valid"));
        }
        final ChangeFrequency changeFrequency = ChangeFrequency.find(changeFrequencyString);
        if (!isDouble(priorityString)) {
            return pure(badRequest("Priority is not valid"));
        }
        final Double priority = Double.valueOf(priorityString);
        if (!isNumeric(depthString)) {
            return pure(badRequest("Depth is not valid"));
        }
        final Integer depth = Integer.valueOf(depthString);
        return siteMapService.generate(url, changeFrequency, priority, depth)
                .map(urls -> {
                    List<SiteMapUrlSet> sets = new ArrayList<>(urls.size() / SITE_MAP_LIMIT + 1);
                    for (int i = 0; i < urls.size(); i += SITE_MAP_LIMIT) {
                        SiteMapUrlSet siteMapUrlSet = new SiteMapUrlSet();
                        siteMapUrlSet.setUrls(urls.subList(i, i + min(SITE_MAP_LIMIT, urls.size() - i)));
                        sets.add(siteMapUrlSet);
                    }
                    return sets;
                }).flatMap(urlSets -> {
                    final List<UrlSetData> urlSetsContent = urlSets.parallelStream()
                            .map(xStream::toXML)
                            .map(String::getBytes)
                            .map(UrlSetData::new)
                            .collect(toList());
                    GeneratedSiteMapEntity generatedSiteMapEntity = new GeneratedSiteMapEntity();
                    generatedSiteMapEntity.setUrl(url.toString());
                    generatedSiteMapEntity.setUrlSets(urlSetsContent);
                    return siteMapService.save(generatedSiteMapEntity).map(saved -> {
                        if (saved) {
                            return ok(toJson(generatedSiteMapConverter.toDto(generatedSiteMapEntity)));
                        } else {
                            return badRequest("Can't save result");
                        }
                    });
                });
    }

    private String addHttpIfNeed(String url) {
        if (url.split("://").length < 2) {
            return "http://" + url;
        }
        return url;
    }

    private boolean isDefaultPort(int port) {
        return port == -1 || port == 80;
    }
}
