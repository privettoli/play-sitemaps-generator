package com.uawebchallenge.backend.controller.impl;

import com.uawebchallenge.backend.controller.PageController;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

@org.springframework.stereotype.Controller
public class PageControllerImpl extends Controller implements PageController {
    @Override
    public Result index() {
        return ok(index.render());
    }
}
