package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.CREATE;

@Log4j2
@Component
@RequiredArgsConstructor
public class StartHandler implements Handler {
    private final JpaUserRepository userRepository;

    @Value("${bot.name}")
    private String botUsername;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        UserState state = user.getUserState();
        switch (state) {
            case NONE:
                return welcomeMessage(user);
            case START:
                return startMessage(user);
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> startMessage(User user) {
        user.setBotState(BotState.REG);
        user.setUserState(UserState.SELECT_ROLE);
        return List.of(HandlerHelper.selectRole(userRepository.save(user)));
    }

    private List<PartialBotApiMethod<? extends Serializable>> welcomeMessage(User user) {
        user.setUserState(UserState.START);
        userRepository.save(user);
        log.info("Set user({}): user_state - {}", user.getId(), UserState.START);

        return List.of(TelegramUtil.createMessageTemplate(user)
                .setText(String.format("Привет %s!!!%nМеня зовут @%s :D%n" +
                        "Я был создан для работы со студентами и педагогами ЧТОТиБ.%n" +
                        "Давай создадим твою анкету?!", user.getUserName(), botUsername))
                .setReplyMarkup(TelegramUtil.createReplyKeyboardMarkup()
                        .setKeyboard(List.of(TelegramUtil.createKeyboardRow(List.of(CREATE)))))
                .enableMarkdown(true));
    }

    @Override
    public BotState operatedBotState() {
        return BotState.START;
    }

    @Override
    public List<Role> operatedUserRoles() {
        return List.of(Role.NONE);
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.NONE,
                UserState.START
        );
    }
}
