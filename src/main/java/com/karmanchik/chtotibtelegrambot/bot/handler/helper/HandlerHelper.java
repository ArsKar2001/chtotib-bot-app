package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.Role;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_STUDENT;
import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_TEACHER;

public class HandlerHelper {
    private HandlerHelper() {
    }

    public static PartialBotApiMethod<? extends Serializable> selectRole(User user) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow row = TelegramUtil.createKeyboardRow(List.of(
                ROLE_STUDENT,
                ROLE_TEACHER
        ));
        markup.setKeyboard(List.of(row));
        markup.setOneTimeKeyboard(true);

        return TelegramUtil.createMessageTemplate(user)
                .setText("Кто ты?")
                .setReplyMarkup(markup);
    }

    public static PartialBotApiMethod<? extends Serializable> mainMessage(User user) {
        Role role = user.getRole();

        return TelegramUtil.createMessageTemplate(user)
                .setText("1.\tУзнать расписание");
    }

    public static boolean isNumeric(String strNum) {
        if (strNum != null) try {
            int i = Integer.parseInt(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
        else return false;
    }
}
