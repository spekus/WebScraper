package com.mycompany.app.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("scraper.vinted")
public class ScraperProperties {

    private String address;
    private String sourceEmailAddress;
    private String password;
    private String targetEmailAddress;
    private String filename;

}
