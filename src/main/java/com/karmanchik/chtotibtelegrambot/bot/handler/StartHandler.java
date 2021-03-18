package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.BotState;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.UserState;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class StartHandler implements Handler {
    @Value("${bot.name}")
    private String botUsername;

    private final UserService userService;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage welcomeMessage = TelegramUtil.createMessageTemplate(user)
                .setText(String.format(
                        "Привет!%nМеня зовут %s :D%nЯ был создан для работы со студентами и педагогами ЧТОТиБ.", botUsername
                ))
                .enableMarkdown(false);

        user.setUserStateId(Constants.UserState.SELECT_ROLE);
        user.setBotStateId(Constants.BotState.REG);
        userService.save(user);

        return List.of(welcomeMessage, RegistrationHandler.selectRole(user).get(0));
    }

    @Override
    public Integer operatedBotStateId() {
        return Constants.BotState.START;
    }

    @Override
    public List<Integer> operatedUserListStateId() {
        return List.of(Constants.UserState.NONE);
    }
}
