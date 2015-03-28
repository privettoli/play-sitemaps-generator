package com.uawebchallenge.backend.controller;

import play.libs.F.Promise;
import play.mvc.Result;

public interface SiteMapController {
    Promise<Result> get(Long id);

    Promise<Result> generate(String incomingUrl, String sendToEmail, String defaultChangeFrequencyString,
                             String defaultPriorityString, String depthString);
}
