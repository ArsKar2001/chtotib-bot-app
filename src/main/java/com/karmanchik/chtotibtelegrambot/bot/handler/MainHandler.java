package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.command.MainCommand;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public abstract class MainHandler implements Handler {
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) throws ResourceNotFoundException {
        if (MainCommand.isCommand(message)) {
            switch (MainCommand.get(message)) {
                case COMMAND_1:
                    return getTimetableNextDay(user);
                case COMMAND_2:
                    return getTimetableFull(user);
                case COMMAND_3:
                    return getTimetableOther(user);
                case COMMAND_4:
                    return editProfile(user);
            }
        }
        return List.of(HandlerHelper.mainMessage(user));
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableOther(User user) {
        return null;
    }

    protected abstract List<PartialBotApiMethod<? extends Serializable>> editProfile(User user);

    protected abstract List<PartialBotApiMethod<? extends Serializable>> getTimetableFull(User user);

    protected abstract List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(User user);
}
