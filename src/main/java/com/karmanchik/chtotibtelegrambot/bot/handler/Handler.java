package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {

    /**
     * Обработчик входящих сообщений пользователя.
     * @param chatUser пользователь
     * @param message сообщение
     * @return
     */
    List<PartialBotApiMethod<? extends Serializable>> handle(ChatUser chatUser, String message) throws ResourceNotFoundException;

    BotState operatedBotState();

    List<Role> operatedUserRoles();

    List<UserState> operatedUserSate();

    default String getName() {
        return this.getClass().getName();
    }
}
