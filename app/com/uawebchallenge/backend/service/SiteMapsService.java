package com.uawebchallenge.backend.service;

import play.libs.F.Promise;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

/**
 * Created by Anatoliy Papenko on 3/24/15.
 */
public interface SiteMapsService {
    Promise<List<SiteMapUrl>> generate(URL link, ChangeFrequency defaultChangeFrequency, Double priority, Integer depth);
}
