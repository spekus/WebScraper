package com.mycompany.app.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("spedlite.email")
public class Properties {

    private String address;
    private String password;
    private String filename;

}
