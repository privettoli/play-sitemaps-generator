package com.uawebchallenge.backend.controller.impl;

import com.uawebchallenge.backend.controller.SiteMapController;
import com.uawebchallenge.backend.service.ChangeFrequency;
import com.uawebchallenge.backend.service.SiteMapsService;
import lombok.experimental.FieldDefaults;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.validation.Constraints.EmailValidator;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;

import java.net.URI;
import java.net.URL;

import static com.uawebchallenge.backend.service.ChangeFrequency.hasValue;
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
    SiteMapsService siteMapsService;
    UrlValidator urlValidator;
    EmailValidator emailValidator;

    @Autowired
    public SiteMapControllerImpl(SiteMapsService siteMapsService, UrlValidator urlValidator,
                                 EmailValidator emailValidator) {
        this.siteMapsService = siteMapsService;
        this.urlValidator = urlValidator;
        this.emailValidator = emailValidator;
    }

    @Override
    public Promise<Result> get(Long id) {
        return null;
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
        return siteMapsService.generate(url, changeFrequency, priority, depth)
                .map(data -> ok(toJson(data)));
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
