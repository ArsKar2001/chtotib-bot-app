package com.karmanchik.chtotibtelegrambot.bot.util;

import com.google.inject.internal.cglib.core.$Customizer;
import com.karmanchik.chtotibtelegrambot.jpa.entity.BaseEntity;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.models.BaseModel;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class TelegramUtil {
    private TelegramUtil() {
    }

    public static SendMessage createMessageTemplate(User user) {
        String chatId = user.getChatId().toString();
        return new SendMessage()
                .setChatId(chatId)
                .enableMarkdown(false)
                .setParseMode(ParseMode.HTML);
    }

    public static ReplyKeyboardMarkup createReplyKeyboardMarkup() {
        return new ReplyKeyboardMarkup()
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(false)
                .setSelective(false);
    }

    public static KeyboardRow createKeyboardRow(List<String> strings) {
        KeyboardRow row = new KeyboardRow();
        row.addAll(strings);
        return row;
    }

    public static Integer getAcademicYear(String message) {
        Calendar calendar = Calendar.getInstance();
        int number = Integer.parseInt(message);
        int now_year = calendar.get(Calendar.YEAR);
        int now_month = calendar.get(Calendar.MONTH);
        int academicYear = now_year - number;
        return (now_month > Month.SEPTEMBER.getValue()) ? academicYear + 1 : academicYear;
    }

    public static List<List<InlineKeyboardButton>> createInlineKeyboardButtons(List<? extends BaseModel> models, Integer countButtonInRow) {
        List<List<InlineKeyboardButton>> listList = new LinkedList<>();
        List<InlineKeyboardButton> buttonsLine = new LinkedList<>();

        models.stream()
                .map(model -> TelegramUtil.createInlineKeyboardButton(model.getName(), model.getId().toString()))
                .forEach(button -> {
                    buttonsLine.add(button);
                    if (buttonsLine.size() % countButtonInRow == 0) {
                        listList.add(new LinkedList<>(buttonsLine));
                        buttonsLine.clear();
                    }
                });
        listList.add(buttonsLine);
        return listList;
    }

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        return new InlineKeyboardButton()
                .setText(text)
                .setCallbackData(command);
    }

    public static List<PartialBotApiMethod<? extends Serializable>> createSelectCourseButtonPanel(User user) {
        List<String> values = Courses.getKeys();
        Collections.sort(values);

        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow row = TelegramUtil.createKeyboardRow(values);
        markup.setKeyboard(List.of(row));

        return List.of(TelegramUtil.createMessageTemplate(user)
                .setText("Выбери курс...")
                .setReplyMarkup(markup));
    }
}
