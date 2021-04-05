package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
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
public class TimetableTeacherHandler implements Handler {
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
            case SELECT_TEACHER:
                break;
            case INPUT_TEXT:
                break;
        }
        return Collections.emptyList();
    }

    public static List<PartialBotApiMethod<? extends Serializable>> start(User user) {
        return List.of(
                HandlerHelper.inputMessage(user, "Введите фамилию...")
        );
    }

    @Override
    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.INPUT_TEXT,
                UserState.SELECT_TEACHER
        );
    }
}
