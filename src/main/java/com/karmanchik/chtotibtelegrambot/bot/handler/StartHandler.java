package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.Helper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
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
    private final UserService userService;

    @Value("${bot.name}")
    private String botUsername;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage welcome = TelegramUtil.createMessageTemplate(user)
                .setText(String.format(
                        "Привет!%nМеня зовут %s :D%nЯ был создан для работы со студентами и педагогами ЧТОТиБ.", botUsername));
        user.setBotState(BotState.REG);
        log.info("Set user({}): bot_state - {}", user.getId(), BotState.REG);
        user.setUserState(UserState.SELECT_ROLE);
        log.info("Set user({}): user_state - {}", user.getId(), BotState.REG);

        User save = userService.save(user);
        return List.of(
                welcome,
                Helper.selectRole(save)
        );
    }

    @Override
    public BotState operatedBotState() {
        return BotState.START;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(UserState.NONE);
    }
}
