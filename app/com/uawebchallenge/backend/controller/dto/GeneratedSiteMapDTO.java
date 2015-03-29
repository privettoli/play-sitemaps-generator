package com.uawebchallenge.backend.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created by Anatoliy Papenko on 3/29/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class GeneratedSiteMapDTO {
    String downloadAsZipUrl;
    String url;
}
