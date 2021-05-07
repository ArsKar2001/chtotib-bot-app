package com.karmanchik.chtotibtelegrambot.bot.daemons;

import com.karmanchik.chtotibtelegrambot.bot.Bot;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.DateHelper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.JpaChatUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class CheckDateDaemon {
    private final JpaChatUserRepository userRepository;
    private static final List<ChatUser> TEMP_CHAT_USERS = new ArrayList<>();
    private final Bot bot;

    @Value("${bot.daemons.period}")
    private int period;

    public CheckDateDaemon(Bot bot, JpaChatUserRepository userRepository) {
        this.bot = bot;
        this.userRepository = userRepository;
    }

    public void start() {
        log.info("Start {} ...", getClass().getName());
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            LocalDate nextDate = DateHelper.getNextSchoolDate();
            LocalDate nowDate = LocalDate.now();

            if (nextDate.getDayOfYear() > nowDate.getDayOfYear()) {
                var users = userRepository.findAllById(List.of(120595));
                users.stream()
                        .filter(TEMP_CHAT_USERS::contains)
                        .filter(user -> user.getUserState() == UserState.NONE)
                        .filter(user -> user.getBotState() == BotState.AUTHORIZED)
                        .forEach(user -> {
                            try {
                                TEMP_CHAT_USERS.add(user);
                                bot.execute(TelegramUtil.createMessageTemplate(user)
                                        .text("Доступно расписание на следующий учебный день.\n" +
                                                "Чтобы узнать, нажми кнопку - \"1\".")
                                        .replyMarkup(TelegramUtil.createReplyKeyboardMarkup()
                                                .oneTimeKeyboard(false)
                                                .keyboardRow(TelegramUtil.createKeyboardRow(List.of("Понятно")))
                                                .build())
                                        .build());
                            } catch (TelegramApiException e) {
                                log.error(e.getMessage(), e);
                            }
                        });
            } else if (!TEMP_CHAT_USERS.isEmpty()) {
                TEMP_CHAT_USERS.clear();
            }

        }, 0, period, TimeUnit.SECONDS);
    }
}
