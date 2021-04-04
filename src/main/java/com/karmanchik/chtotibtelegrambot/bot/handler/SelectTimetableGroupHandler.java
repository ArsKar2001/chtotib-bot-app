package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class SelectTimetableGroupHandler implements Handler {
    private final UserService userService;

    /**
     * Обработчик входящих сообщений пользователя.
     *
     * @param user    пользователь
     * @param message сообщение
     * @return
     */
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        switch (user.getUserState()) {
            case SELECT_COURSE:
                break;
            case SELECT_GROUP:
                break;
        }
        return Collections.emptyList();
    }

    public static List<PartialBotApiMethod<? extends Serializable>> start(User user) {
        return TelegramUtil.createSelectCourseButtonPanel(user);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return null;
    }
}
