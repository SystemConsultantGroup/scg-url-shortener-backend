package com.scg.shortener.global.config.routing;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.scg.shortener.dto.request.GetUrlsRequest;

@Configuration
public class WebConfig extends DelegatingWebMvcConfiguration {

    private final Environment env;

    public WebConfig(Environment env) {
        this.env = env;
    }

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new CustomRequestMappingHandlerMapping(env);
    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, GetUrlsRequest.SortBy.class, GetUrlsRequest.SortBy::fromString);
        super.addFormatters(registry);
    }
}
