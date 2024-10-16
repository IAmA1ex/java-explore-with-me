package ru.practicum.mainservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum.mainservice", "ru.practicum.statsclient"})
public class MainServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }

}
