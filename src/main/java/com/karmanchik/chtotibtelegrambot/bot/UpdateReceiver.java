package com.karmanchik.chtotibtelegrambot.bot;

import com.karmanchik.chtotibtelegrambot.bot.handler.Handler;
import com.karmanchik.chtotibtelegrambot.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotBlank;
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
                final Message message = update.getMessage();
                log.debug("!!!! log debug UpdateReceiver: получили Message - " + message.toString());
                final int chatId = message.getFrom().getId();
                log.debug("!!!! log debug UpdateReceiver: получили chatId - " + chatId);
                final String userName = message.getChat().getUserName();
                log.debug("!!!! log debug UpdateReceiver: получили userName - {}", userName);
                final int messageId = message.getMessageId();
                log.debug("!!!! log debug UpdateReceiver: получили messageId - " + messageId);
                final User user = userService.findByChatIdAndName(chatId, userName);
                log.debug("!!!! log debug UpdateReceiver: user - " + user.toString());
                user.setBotLastMessageId(messageId);
                User sUser = userService.save(user);
                return getHandlerByState(sUser).handle(sUser, message.getText());
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                log.debug("!!!! log debug UpdateReceiver: получили Message - " + callbackQuery.toString());
                final int messageId = callbackQuery.getMessage().getMessageId();
                log.debug("!!!! log debug UpdateReceiver: получили messageId - " + messageId);
                final int chatId = callbackQuery.getFrom().getId();
                log.debug("!!!! log debug UpdateReceiver: получили chatId - " + chatId);
                final String userName = callbackQuery.getMessage().getChat().getUserName();
                log.debug("!!!! log debug UpdateReceiver: получили userName - {}", userName);
                final User user = userService.findByChatIdAndName(chatId, userName);
                log.debug("!!!! log debug UpdateReceiver: user - " + user.toString());
                user.setBotLastMessageId(messageId);
                User sUser = userService.save(user);
                return getHandlerByState(sUser).handle(sUser, callbackQuery.getData());
            }
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Handler getHandlerByState(@NotBlank User user) {
        return handlers.stream()
                .filter(h -> h.operatedUserListStateId().stream()
                        .anyMatch(state -> state.equals(user.getUserStateId())))
                .filter(h -> h.operatedBotStateId().equals(user.getBotStateId()))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
