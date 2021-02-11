package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.BotState;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.UserState;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.List;


@Component
public class StartHandler implements Handler {
    @Value("${bot.name}")
    private String botUsername;

    private final UserService userService;

    public StartHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage welcomeMessage = TelegramUtil.createMessageTemplate(user)
                .setText(String.format(
                        "Привет!%nМеня зовут %s :D%nЯ был создан для работы со студентами и педагогами ЧТОТиБ.", botUsername
                ))
                .enableMarkdown(false);

        user.setUserStateId(UserState.Instance.SELECT_ROLE.getId());
        user.setBotStateId(BotState.Instance.REG.getId());
        userService.save(user);

        return List.of(welcomeMessage, RegistrationHandler.selectRole(user).get(0));
    }

    @Override
    public BotState.Instance operatedBotState() {
        return BotState.Instance.START;
    }

    @Override
    public List<UserState.Instance> operatedUserListState() {
        return List.of(UserState.Instance.NONE);
    }
}
