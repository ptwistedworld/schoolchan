package com.schoolchan;

import com.schoolchan.config.SchoolChanProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SchoolChanProperties.class)
public class SchoolChanApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchoolChanApplication.class, args);
    }
}
