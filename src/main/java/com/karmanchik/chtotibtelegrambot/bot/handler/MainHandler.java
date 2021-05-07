package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.command.MainCommand;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public abstract class MainHandler implements Handler {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(ChatUser chatUser, String message) {
        if (MainCommand.isCommand(message)) {
            switch (MainCommand.get(message)) {
                case COMMAND_1:
                    return getTimetableNextDay(chatUser);
                case COMMAND_2:
                    return getTimetableFull(chatUser);
                case COMMAND_3:
                    return getTimetableOther(chatUser);
                case COMMAND_4:
                    return editProfile(chatUser);
            }
        }
        return List.of(HandlerHelper.mainMessage(chatUser));
    }

    protected abstract List<PartialBotApiMethod<? extends Serializable>> getTimetableOther(ChatUser chatUser);

    protected abstract List<PartialBotApiMethod<? extends Serializable>> editProfile(ChatUser chatUser);

    protected abstract List<PartialBotApiMethod<? extends Serializable>> getTimetableFull(ChatUser chatUser);

    protected abstract List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(ChatUser chatUser);
}
