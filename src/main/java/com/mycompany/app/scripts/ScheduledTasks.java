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

    @Scheduled(fixedRate = 1000000)
    public void reportCurrentTime() {

        log.info("reportCurrentTime is running");

        try {
            jobScrapper.scanForChanges();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
