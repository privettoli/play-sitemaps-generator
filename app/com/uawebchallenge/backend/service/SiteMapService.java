package com.uawebchallenge.backend.service;

import com.uawebchallenge.backend.domain.GeneratedSiteMapEntity;
import com.uawebchallenge.backend.domain.SiteMapUrl;
import play.libs.F.Promise;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Created by Anatoliy Papenko on 3/24/15.
 */
public interface SiteMapService {
    Promise<Optional<GeneratedSiteMapEntity>> findBy(Long id);

    Promise<Boolean> save(GeneratedSiteMapEntity generatedSiteMapEntity);

    Promise<List<SiteMapUrl>> generate(URL link, ChangeFrequency defaultChangeFrequency, Double priority, Integer depth);

    byte[] packageToZip(GeneratedSiteMapEntity generatedSiteMapEntity) throws IOException;
}
