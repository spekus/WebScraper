package com.mycompany.app.scripts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    JobScrapper jobScrapper;

    @Scheduled(fixedDelayString = "${scraper.vinted.scraping-interval-millisec}")
    void reportCurrentTime() {

        log.info("reportCurrentTime is running");

        try {
            jobScrapper.checkForChanges();
        } catch (IOException e) {
            log.error("reportCurrentTime() crashed while running jobScrapper.checkForChanges(), duo to IOException " + e);
        }

    }
}
