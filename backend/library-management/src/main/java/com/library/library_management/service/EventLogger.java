package com.library.library_management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventLogger {

    private static final Logger log = LoggerFactory.getLogger("LIBRARY_EVENTS");

    public void info(String event, String actor, String details) {
        log.info("EVENT={} actor={} {}", event, actor, details);
    }

    public void warn(String event, String actor, String details) {
        log.warn("EVENT={} actor={} {}", event, actor, details);
    }

    public void error(String event, String actor, String details, Throwable throwable) {
        log.error("EVENT={} actor={} {}", event, actor, details, throwable);
    }
}
