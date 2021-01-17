package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.model.State;
import com.karmanchik.chtotibtelegrambot.repository.JpaUserRepository;
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

    private final JpaUserRepository userRepository;

    public StartHandler(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage welcomeMessage = TelegramUtil.createMessageTemplate(user)
                .setText(String.format(
                        "Привет!%nМеня зовут %s :D%nЯ был создан для работы со студентами и педагогами ЧТОТиБ.", botUsername
                ))
                .enableMarkdown(false);

        user.setUserState(State.SELECT_ROLE);
        user.setBotState(State.REG);
        userRepository.save(user);

        return List.of(welcomeMessage, RegistrationHandler.selectRole(user).get(0));
    }

    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<State> operatedUserListState() {
        return List.of(State.NONE);
    }
}
