package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelperService;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.JpaChatUserRepository;
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
    private final JpaChatUserRepository userRepository;

    @Value("${bot.name}")
    private String botUsername;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(ChatUser chatUser, String message) {
        UserState state = chatUser.getUserState();
        switch (state) {
            case NONE:
                return welcomeMessage(chatUser);
            case START:
                return startMessage(chatUser);
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> startMessage(ChatUser chatUser) {
        chatUser.setBotState(BotState.REG);
        chatUser.setUserState(UserState.SELECT_ROLE);
        return List.of(HandlerHelperService.selectRole(userRepository.save(chatUser)));
    }

    private List<PartialBotApiMethod<? extends Serializable>> welcomeMessage(ChatUser chatUser) {
        chatUser.setUserState(UserState.START);
        userRepository.save(chatUser);
        log.info("Set chatUser({}): user_state - {}", chatUser.getId(), UserState.START);

        return List.of(TelegramUtil.createMessageTemplate(chatUser)
                .text(String.format("Привет %s!!!%nМеня зовут @%s :D%n" +
                        "Я был создан для работы со студентами и педагогами ЧТОТиБ.%n" +
                        "Давай создадим твою анкету?!", chatUser.getUserName(), botUsername))
                .replyMarkup(TelegramUtil.createReplyKeyboardMarkup()
                        .keyboardRow(TelegramUtil.createKeyboardRow(List.of(CREATE)))
                        .build())
                .build());
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
