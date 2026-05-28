package com.scg.shortener.global.config.routing;

import org.springframework.web.servlet.mvc.condition.RequestCondition;
import jakarta.servlet.http.HttpServletRequest;

public class HostRequestCondition implements RequestCondition<HostRequestCondition> {

    private final String expectedHost;

    public HostRequestCondition(String expectedHost) {
        this.expectedHost = expectedHost;
    }

    @Override
    public HostRequestCondition combine(HostRequestCondition other) {
        return other; // method level overrides class level
    }

    @Override
    public HostRequestCondition getMatchingCondition(HttpServletRequest request) {
        String actualHost = request.getHeader("Host");
        if (actualHost != null && actualHost.startsWith(expectedHost)) {
            return this;
        }
        return null;
    }

    @Override
    public int compareTo(HostRequestCondition other, HttpServletRequest request) {
        return 0;
    }
}
