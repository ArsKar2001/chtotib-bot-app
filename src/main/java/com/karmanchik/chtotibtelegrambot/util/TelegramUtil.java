package com.karmanchik.chtotibtelegrambot.util;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.model.WeekType;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.MainCommand.*;

public class TelegramUtil {

    public static final Map<Integer, String> DAYS_OF_WEEK = Map.of(
            0, "Понедельник",
            1, "Вторник",
            2, "Среда",
            3, "Четверг",
            4, "Пятница",
            5, "Суббота",
            6, "Воскресенье"
    );


    public static boolean isEvenWeek(Calendar date) {
        int week = date.get(Calendar.WEEK_OF_YEAR);
        return (week % 2 == 0);
    }


    public static Calendar getNextDate() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int days = calendar.get(Calendar.DAY_OF_YEAR);
        int _days = days;
        if (dayOfWeek >= 5) {
            days -= dayOfWeek - 1;
            days += 7;
            days -= _days;
            calendar.add(Calendar.DAY_OF_YEAR, days);
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar;
    }

    public static WeekType getWeekType(Calendar next) {
        return isEvenWeek(next) ? WeekType.UP : WeekType.DOWN;
    }

    public static int getNextDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int nextDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return (nextDayOfWeek >= 5) ? 0 : nextDayOfWeek;
    }

    public static SendMessage createMessageTemplate(User user) {
        return createMessageTemplate(String.valueOf(user.getChatId()))
                .enableMarkdown(false)
                .setParseMode(ParseMode.HTML);
    }
    

    public static SendMessage createMessageTemplate(String chatId) {
        return new SendMessage()
                .setChatId(chatId)
                .enableMarkdown(false);
    }

    public static List<List<InlineKeyboardButton>> createGroupListInlineKeyboardButton(List<Group> groupList, Integer countButtonInRow) {
        List<List<InlineKeyboardButton>> listList = new ArrayList<>();
        List<InlineKeyboardButton> buttonsLine = new ArrayList<>();
        for (Group group : groupList) {
            buttonsLine.add(TelegramUtil.createInlineKeyboardButton(group.getGroupName(), group.getId().toString()));
            if (buttonsLine.size() % countButtonInRow == 0) {
                listList.add(buttonsLine);
                buttonsLine = new ArrayList<>();
            }
        }
        listList.add(buttonsLine);
        return listList;
    }

    public static List<List<InlineKeyboardButton>> createTeacherListInlineKeyboardButton(List<String> teachers, Integer countButtonInRow) {
        List<List<InlineKeyboardButton>> listList = new ArrayList<>();
        List<InlineKeyboardButton> buttonsLine = new ArrayList<>();
        for (String teacher : teachers) {
            buttonsLine.add(TelegramUtil.createInlineKeyboardButton(teacher, teacher));
            if (buttonsLine.size() % countButtonInRow == 0) {
                listList.add(buttonsLine);
                buttonsLine = new ArrayList<>();
            }
        }
        listList.add(buttonsLine);
        return listList;
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        return new InlineKeyboardButton()
                .setText(text)
                .setCallbackData(command);
    }

    public static ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        return new ReplyKeyboardMarkup()
                .setResizeKeyboard(true).setOneTimeKeyboard(false).setSelective(false);
    }

    public static KeyboardRow createKeyboardRow(List<String> strings) {
        KeyboardRow row = new KeyboardRow();
        row.addAll(strings);
        return row;
    }

    public static PartialBotApiMethod<? extends Serializable> mainMessage(User user) {

        ReplyKeyboardMarkup replyKeyboardMarkup = createReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(COM_1.getValue());
        row.add(COM_2.getValue());
        row.add(COM_3.getValue());
        row.add(COM_4.getValue());

        keyboardRowList.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return TelegramUtil.createMessageTemplate(user)
                .setText(
                        "1\t-\tРасписание на завтра\n" +
                                "2\t-\tВсе расписание\n" +
                                "3\t-\tСправочное сообщение\n" +
                                "4\t-\tИзменить профиль\n"
                )
                .setReplyMarkup(replyKeyboardMarkup);
    }

    @Getter
    public enum MainCommand {
        COM_1("1"),
        COM_2("2"),
        COM_3("3"),
        COM_4("4");
        private final String value;

        MainCommand(String value) {
            this.value = value;
        }
    }
}
