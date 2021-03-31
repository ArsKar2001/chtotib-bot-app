package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper.MainCommand;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainHandler implements Handler {
    private final UserService userService;
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (isCommand(message)) {
            MainCommand command = MainCommand.get(message);
            switch (command) {
                case COMMAND_1:
                    return getTimetableNextDay(user);
                case COMMAND_2:
                    break;
                case COMMAND_3:
                    break;
                case COMMAND_4:
                    return editProfile(user);
            }
        }
        return List.of(HandlerHelper.mainMessage(user));
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(User user) {

        return null;
    }

    private List<PartialBotApiMethod<? extends Serializable>> editProfile(User user) {
        user.setBotState(BotState.REG);
        user.setUserState(UserState.SELECT_ROLE);
        return List.of(
                HandlerHelper.selectRole(userService.save(user)));
    }

    private boolean isCommand(String message) {
        return MainCommand.getMap().containsKey(message);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.NONE,
                UserState.INPUT_TEXT
        );
    }
}
