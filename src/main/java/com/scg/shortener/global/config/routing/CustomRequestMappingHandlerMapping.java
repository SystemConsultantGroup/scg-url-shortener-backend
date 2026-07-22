package com.scg.shortener.global.config.routing;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final Environment env;

    public CustomRequestMappingHandlerMapping(Environment env) {
        this.env = env;
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        DynamicHostRoute annotation = AnnotationUtils.findAnnotation(handlerType, DynamicHostRoute.class);
        return createCondition(annotation);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        DynamicHostRoute annotation = AnnotationUtils.findAnnotation(method, DynamicHostRoute.class);
        return createCondition(annotation);
    }

    private RequestCondition<?> createCondition(DynamicHostRoute annotation) {
        if (annotation == null) {
            return null;
        }
        String expectedHost = env.getProperty(annotation.value());
        return new HostRequestCondition(expectedHost);
    }
}
