package com.example.workflow.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public FilterRegistrationBean<Filter> corsFilter(JerseyApplicationPath applicationPath) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setName("camunda-cors");
        CorsFilter corsFilter = new CorsFilter();
        registration.setFilter(corsFilter);
        registration.setOrder(0);

        String restApiPathPattern = applicationPath.getUrlMapping();
        registration.addUrlPatterns(restApiPathPattern);

        registration.addInitParameter(CorsFilter.PARAM_CORS_ALLOWED_ORIGINS, "*");
        registration.addInitParameter(CorsFilter.PARAM_CORS_ALLOWED_METHODS, "GET,POST,HEAD,OPTIONS,PUT,DELETE");

        return registration;
    }
}
