package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_STUDENT;
import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_TEACHER;

public class Helper {
    private Helper() {}

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
        return TelegramUtil.createMessageTemplate(user)
                .setText("Поздравляю! :)");
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
