package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import com.karmanchik.chtotibtelegrambot.service.GroupService;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.Month;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.entity.constants.Constants.Bot.AUTHORIZED;
import static com.karmanchik.chtotibtelegrambot.entity.constants.Constants.Bot.REG;
import static com.karmanchik.chtotibtelegrambot.entity.constants.Constants.User.*;
import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.createReplyKeyboardMarkup;
import static java.lang.Integer.parseInt;

@Log4j2
@Component
@RequiredArgsConstructor
public class Helper {
    public static final String ACCEPT = "сохранить";
    public static final String CHANGE = "изменить";
    public static final String CANCEL = "назад";

    private final UserService userService;
    private final GroupService groupService;

    public List<String> getListFullTeachers(String message) {
        List<String> teachers = groupService.findAllTeachersByName(message.toLowerCase());
        List<String> _teachers = new ArrayList<>();

        for (String s : teachers) {
            if (s.contains("||")) {
                String[] split = s.split("\\|\\|", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else if (s.contains(",")) {
                String[] split = s.split(",", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else if (s.contains("║")) {
                String[] split = s.split("║", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else if (s.contains("‖")) {
                String[] split = s.split("‖", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else {
                _teachers.add(s);
            }
        }

        Set<String> set = new HashSet<>(_teachers);
        _teachers.clear();
        _teachers.addAll(set);
        return _teachers;
    }

    int getAcademicYear(String message) {
        Calendar calendar = Calendar.getInstance();
        int number = parseInt(message);
        int now_year = calendar.get(Calendar.YEAR);
        int now_month = calendar.get(Calendar.MONTH);
        int academicYear = now_year - number;
        return (now_month > Month.SEPTEMBER.getValue()) ? academicYear + 1 : academicYear;
    }

    public List<PartialBotApiMethod<? extends Serializable>> selectGroup(User user, String message) {

        if (Courses.containsKey(message)) {
            final String s = Courses.get(message);
            int academicYear = this.getAcademicYear(s);
            String academicYearSuffix = String.valueOf(academicYear).substring(2);
            List<Group> groupList = groupService.findAllGroupNamesByYearSuffix(academicYearSuffix);

            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            keyboardMarkup.setKeyboard(TelegramUtil.createGroupListInlineKeyboardButton(groupList, 3));

            user.setUserStateId(Constants.User.SELECT_GROUP);
            final User saveUser = userService.save(user);
            return List.of(
                    TelegramUtil.createMessageTemplate(saveUser)
                            .setText("Выбери группу...")
                            .setReplyMarkup(keyboardMarkup)
            );
        } else return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> inputTeacherName(User user) {
        user.setUserStateId(ENTER_NAME);
        User save = userService.save(user);
        return List.of(
                TelegramUtil.createMessageTemplate(save)
                        .setText("Введите фамилию...")
                        .enableMarkdown(false)
        );
    }

    public List<PartialBotApiMethod<? extends Serializable>> createSelectTeacherButtonsPanel(User user, String message) {
        user.setUserStateId(SELECT_TEACHER);
        final User saveUser = userService.save(user);
        List<String> teacherList = getListFullTeachers(message.toLowerCase());

        InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
        markup1.setKeyboard(TelegramUtil.createTeacherListInlineKeyboardButton(teacherList, 2));

        ReplyKeyboardMarkup markup2 = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(CANCEL.toUpperCase()));
        markup2.setKeyboard(List.of(keyboardRow));

        if (!teacherList.isEmpty()) {
            return createMessageItIsYou(saveUser, markup1, markup2);
        } else {
            return createMessageDidNotDefine(saveUser);
        }
    }

    public List<PartialBotApiMethod<? extends Serializable>> createMessageDidNotDefine(User user) {
        String outMessage = "Не смог вас определить :(";
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(outMessage)
                        .enableMarkdown(false),
                cancel(user).get(0)
        );
    }

    public List<PartialBotApiMethod<? extends Serializable>> cancel(User user) {
        user.setUserStateId(SELECT_ROLE);
        user.setBotStateId(REG);
        final User saveUser = userService.save(user);
        return RegistrationHandler.selectRole(saveUser);
    }

    public List<PartialBotApiMethod<? extends Serializable>> accept(User user) {
        user.setUserStateId(NONE);
        user.setBotStateId(AUTHORIZED);
        final User saveUser = userService.save(user);
        return List.of(mainMessage(saveUser));
    }

    public List<PartialBotApiMethod<? extends Serializable>> createMessageItIsYou(User user, InlineKeyboardMarkup markup1, ReplyKeyboardMarkup markup2) {
        String outMessage = "Это Вы...";
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(outMessage)
                        .setReplyMarkup(markup1),
                TelegramUtil.createMessageTemplate(user)
                        .setText("...?")
                        .setReplyMarkup(markup2)
        );
    }


    public static PartialBotApiMethod<? extends Serializable> mainMessage(User user) {
        final Set<String> keySet = MainHandler.Command.COMMAND_MAP.keySet();
        ReplyKeyboardMarkup replyKeyboardMarkup = createReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        keySet.forEach(row::add);
        keyboardRowList.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        return TelegramUtil.createMessageTemplate(user)
                .setText("1\t-\tРасписание на завтра\n" +
                        "2\t-\tВсе расписание\n" +
                        "3\t-\tСправочное сообщение\n" +
                        "4\t-\tИзменить профиль\n")
                .setReplyMarkup(replyKeyboardMarkup);
    }

    List<PartialBotApiMethod<? extends Serializable>> createSelectCourseButtonsPanel(User user) {
        List<String> values = Courses.getKeys();
        Collections.sort(values);

        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(values);
        markup.setKeyboard(List.of(keyboardRow));

        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Выбери курс...")
                        .setReplyMarkup(markup)
        );
    }
}
