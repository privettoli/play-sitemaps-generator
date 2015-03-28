package com.uawebchallenge.backend.config;

import com.uawebchallenge.backend.SpringConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
    private ConfigurableApplicationContext context;

    @Override
    public void onStart(Application application) {
        super.onStart(application);
        context = SpringApplication.run(SpringConfiguration.class);
    }

    @Override
    public void onStop(final Application app) {
        context.close();
        super.onStop(app);
    }

    @Override
    public <A> A getControllerInstance(Class<A> clazz) {
        return context.getBean(clazz);
    }
}
