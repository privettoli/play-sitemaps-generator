package com.uawebchallenge.backend;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import play.data.validation.Constraints.EmailValidator;
import play.libs.ws.WSClient;
import scala.concurrent.ExecutionContext;

import static play.libs.Akka.system;

@Configuration
@ComponentScan
@EnableJpaAuditing
@EnableJpaRepositories
@EnableAutoConfiguration
public class SpringConfiguration {

    @Bean
    @Qualifier("databaseExecutionContext")
    public ExecutionContext executionContext() {
        return system().dispatchers().lookup("akka.db-dispatcher");
    }

    @Bean
    @Qualifier("defaultExecutionContext")
    public ExecutionContext defaultExecutionContext() {
        return system().dispatcher();
    }

    @Bean
    public UrlValidator urlValidator() {
        return new UrlValidator();
    }

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }
}
