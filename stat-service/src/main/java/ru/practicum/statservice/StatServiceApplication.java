package ru.practicum.statservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Класс приложения для запуска Stat Service.
 */
@SpringBootApplication
public class StatServiceApplication {

    /**
     * Запуск приложения Stat Service.
     *
     * @param args параметры запуска
     */
    public static void main(final String[] args) {
        SpringApplication.run(StatServiceApplication.class, args);
    }

}
