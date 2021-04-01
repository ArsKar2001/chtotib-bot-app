package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.Role;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

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

    public static PartialBotApiMethod<? extends Serializable> inputMessage(User user, String text) {
        return TelegramUtil.createMessageTemplate(user)
                .setText(text)
                .enableMarkdown(true);
    }


    public static PartialBotApiMethod<? extends Serializable> mainMessage(User user) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        LocalDate nextSchoolDate = DateHelper.getNextSchoolDate();
        String name = nextSchoolDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
        Role role = user.getRole();
        markup.setKeyboard(List.of(
                TelegramUtil.createKeyboardRow(MainCommand.getKeyAll())));
        return role.equals(Role.STUDENT) ?
                TelegramUtil.createMessageTemplate(user)
                        .setText("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tВсе расписание\n" +
                                "3.\tУзнать расписание педагога\n" +
                                "4.\tИзменить анкету")
                        .setReplyMarkup(markup) :
                TelegramUtil.createMessageTemplate(user)
                        .setText("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tВсе расписание\n" +
                                "3.\tУзнать расписание студента\n" +
                                "4.\tИзменить анкету")
                        .setReplyMarkup(markup);
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

    public enum MainCommand {
        COMMAND_1,
        COMMAND_2,
        COMMAND_3,
        COMMAND_4;
        private static final Map<String, MainCommand> COMMAND_MAP = new HashMap<>();

        static {
            Arrays.stream(MainCommand.values()).forEach(c -> {
                String key = String.valueOf(c.ordinal() + 1);
                COMMAND_MAP.put(key, c);
            });
        }

        public static List<String> getKeyAll() {
            return new ArrayList<>(COMMAND_MAP.keySet());
        }

        public static MainCommand get(String key) {
            return COMMAND_MAP.get(key);
        }

        public static Map<String, MainCommand> getMap() {
            return COMMAND_MAP;
        }
    }
}
