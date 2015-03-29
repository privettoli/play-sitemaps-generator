package com.uawebchallenge.backend.converter;

import com.uawebchallenge.backend.controller.dto.GeneratedSiteMapDTO;
import com.uawebchallenge.backend.domain.GeneratedSiteMapEntity;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

/**
 * Created by Anatoliy Papenko on 3/29/15.
 */
@Component
public class GeneratedSiteMapConverter {
    public GeneratedSiteMapDTO toDto(GeneratedSiteMapEntity generatedSiteMapEntity) {
        final GeneratedSiteMapDTO dto = new GeneratedSiteMapDTO();
        dto.setDownloadAsZipUrl(format("/sitemaps/%d.zip", generatedSiteMapEntity.getId()));
        dto.setUrl(generatedSiteMapEntity.getUrl());
        return dto;
    }
}
