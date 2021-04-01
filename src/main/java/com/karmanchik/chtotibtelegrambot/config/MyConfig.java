package com.karmanchik.chtotibtelegrambot.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Log4j2
@Configuration
public class MyConfig {
    @Bean
    public Locale locale() {
        Locale ru = Locale.forLanguageTag("ru");
        log.info("Set locale - {}", ru);
        return ru;
    }
}
