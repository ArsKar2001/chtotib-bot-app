package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {

    /**
     * Обработчик входящих сообщений пользователя.
     * @param user пользователь
     * @param message сообщение
     * @return
     */
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    BotState operatedBotState();

    List<UserState> operatedUserSate();
}
