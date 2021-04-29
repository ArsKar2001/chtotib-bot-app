package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class TimetableGroupHandler implements Handler {


    public static List<PartialBotApiMethod<? extends Serializable>> start(User user) {
        return null;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public BotState operatedBotState() {
        return null;
    }

    @Override
    public List<Role> operatedUserRoles() {
        return null;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return null;
    }
}
