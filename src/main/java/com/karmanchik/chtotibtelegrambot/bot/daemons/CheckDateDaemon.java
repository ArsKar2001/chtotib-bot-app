package com.karmanchik.chtotibtelegrambot.bot.daemons;

import com.karmanchik.chtotibtelegrambot.bot.Bot;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.DateHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class CheckDateDaemon {
    private final JpaUserRepository userRepository;
    private final Bot bot;

    public CheckDateDaemon(Bot bot, JpaUserRepository userRepository) {
        this.bot = bot;
        this.userRepository = userRepository;
    }

    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            LocalDate nextDate = DateHelper.getNextSchoolDate();
            LocalDate now = LocalDate.now();
            if (nextDate.getDayOfYear() > now.getDayOfYear()) {
                List<User> users = userRepository.findAll();
                users.parallelStream()
                        .forEach(user -> {
                            try {
                                bot.execute(TelegramUtil.createMessageTemplate(user)
                                        .setText("Доступно расписание на следующий учебный день.\n" +
                                                "Чтобы узнать, нажми кнопку \"1\"."));
                                bot.execute((SendMessage) HandlerHelper.mainMessage(user));
                            } catch (TelegramApiException e) {
                                log.info(e.getMessage(), e);
                            }
                        });
            }
        }, 12, 1, TimeUnit.HOURS);
    }
}
