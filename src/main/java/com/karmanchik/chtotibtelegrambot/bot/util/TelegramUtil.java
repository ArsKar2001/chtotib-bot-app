package com.karmanchik.chtotibtelegrambot.bot.util;

import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.model.BaseModel;
import com.karmanchik.chtotibtelegrambot.model.Course;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.Month;
import java.util.*;

public class TelegramUtil {
    private TelegramUtil() {
    }

    public static SendMessage createMessageTemplate(ChatUser chatUser) {
        String chatId = chatUser.getChatId().toString();
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

    public static KeyboardRow createKeyboardRow(List<String> values) {
        KeyboardRow row = new KeyboardRow();
        row.addAll(values);
        return row;
    }

    public static Integer getAcademicYear(Course course) {
        Calendar calendar = Calendar.getInstance();
        int number = course.getValue();
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

    public static List<PartialBotApiMethod<? extends Serializable>> createSelectCourseButtonPanel(ChatUser chatUser) {
        List<String> names = Course.names();
        Collections.sort(names);

        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow row = TelegramUtil.createKeyboardRow(names);
        markup.setKeyboard(List.of(row));

        return List.of(TelegramUtil.createMessageTemplate(chatUser)
                .setText("Выбери курс...")
                .setReplyMarkup(markup));
    }
}
