package com.scg.shortener.global.config.routing;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicHostRoute {
    String value(); // property key from application.yml
}
