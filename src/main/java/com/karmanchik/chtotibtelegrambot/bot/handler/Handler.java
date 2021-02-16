package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.BotState;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.UserState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    BotState.Instance operatedBotState();

    List<UserState.Instance> operatedUserListState();
}
