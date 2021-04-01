package com.karmanchik.chtotibtelegrambot.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Log4j2
@Configuration
public class MyConfig {
    @Bean
    public LocaleResolver locale() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        Locale locale = new Locale("ru", "RU");
        slr.setDefaultLocale(locale);
        return slr;
    }
}
