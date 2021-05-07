package com.karmanchik.chtotibtelegrambot;

import com.karmanchik.chtotibtelegrambot.bot.daemons.CheckDateDaemon;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
public class Application {
    private final CheckDateDaemon dateDaemon;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Irkutsk"));
    }

    @PostConstruct
    public void startDaemons() {
        log.info("Запускаем демомнов...");
        dateDaemon.start();
        log.info("Демомноны запущены");
    }
}
