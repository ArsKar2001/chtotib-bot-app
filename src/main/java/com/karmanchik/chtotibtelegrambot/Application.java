package com.karmanchik.chtotibtelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Irkutsk"));
    }
}
