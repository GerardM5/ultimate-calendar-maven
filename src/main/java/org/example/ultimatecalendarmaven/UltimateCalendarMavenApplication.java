package org.example.ultimatecalendarmaven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UltimateCalendarMavenApplication {

    public static void main(String[] args) {
        SpringApplication.run(UltimateCalendarMavenApplication.class, args);
    }

}
