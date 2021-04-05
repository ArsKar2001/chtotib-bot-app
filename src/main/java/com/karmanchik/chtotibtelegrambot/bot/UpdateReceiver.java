package com.karmanchik.chtotibtelegrambot.bot;

import com.karmanchik.chtotibtelegrambot.bot.handler.Handler;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Log4j2
@Component
public class UpdateReceiver {
    private final List<Handler> handlers;
    private final UserService userService;

    public UpdateReceiver(List<Handler> handlers, UserService userService) {
        this.handlers = handlers;
        this.userService = userService;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        try {
            if (isMessageWithText(update)) {
                Message message = update.getMessage();
                log.info("Message - {}", message);
                Integer chatId = message.getFrom().getId();
                log.info("Chat Id - {}", chatId);
                String userName = message.getChat().getUserName();
                log.info("UserName - {}", userName);
                User user = userService.findByChatIdAndUserName(chatId, userName);
                log.info("User - {}", user);
                return getHandlerByState(user).handle(user, message.getText());
            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                log.info("CallbackQuery - {}", callbackQuery);
                Integer chatId = callbackQuery.getFrom().getId();
                log.info("Chat Id - {}", chatId);
                String userName = callbackQuery.getMessage().getChat().getUserName();
                log.info("UserName - {}", userName);
                User user = userService.findByChatIdAndUserName(chatId, userName);
                log.info("User - {}", user);
                return getHandlerByState(user).handle(user, callbackQuery.getData());
            }
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException | ResourceNotFoundException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(User user) {
        return handlers.stream()
                .filter(handler -> handler.operatedBotState() == user.getBotState())
                .filter(handler -> handler.operatedUserSate().stream()
                        .anyMatch(state -> state == user.getUserState()))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
