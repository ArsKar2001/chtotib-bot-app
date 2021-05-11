package com.karmanchik.chtotibtelegrambot.bot;

import com.karmanchik.chtotibtelegrambot.bot.handler.Handler;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.JpaChatUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Log4j2
@Service("updateReceiverService")
public class UpdateReceiverService {
    private final List<Handler> handlers;
    private final JpaChatUserRepository userRepository;

    public UpdateReceiverService(List<Handler> handlers, JpaChatUserRepository userRepository) {
        this.handlers = handlers;
        this.userRepository = userRepository;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        try {
            if (isMessageWithText(update)) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();
                String userName = message.getChat().getUserName();
                ChatUser chatUser = userRepository.findByChatIdAndUserName(chatId, userName)
                        .orElseGet(() -> userRepository.save(ChatUser
                                .builder(chatId, userName)
                                .botState(BotState.START)
                                .userState(UserState.NONE)
                                .role(Role.NONE)
                                .build()));
                log.info("ChatUser - {}", chatUser);
                return getHandlerByUser(chatUser).handle(chatUser, message.getText());
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Long chatId = callbackQuery.getMessage().getChatId();
                String userName = callbackQuery.getMessage().getChat().getUserName();
                ChatUser chatUser = userRepository.findByChatIdAndUserName(chatId, userName)
                        .orElseGet(() -> userRepository.save(ChatUser
                                .builder(chatId, userName)
                                .botState(BotState.START)
                                .userState(UserState.NONE)
                                .role(Role.NONE)
                                .build()));
                log.info("ChatUser - {}", chatUser);
                return getHandlerByUser(chatUser).handle(chatUser, callbackQuery.getMessage().getText());
            }
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException | ResourceNotFoundException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByUser(ChatUser chatUser) {
        return handlers.stream()
                .filter(handler -> handler.operatedBotState() == chatUser.getBotState())
                .filter(handler -> handler.operatedUserRoles().stream()
                        .anyMatch(role -> role == chatUser.getRole()))
                .filter(handler -> handler.operatedUserSate().stream()
                        .anyMatch(state -> state == chatUser.getUserState()))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
