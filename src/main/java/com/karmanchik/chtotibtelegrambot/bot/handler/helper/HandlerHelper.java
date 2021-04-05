package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.Role;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.jpa.models.GroupOrTeacher;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;
import com.karmanchik.chtotibtelegrambot.jpa.service.GroupService;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import com.karmanchik.chtotibtelegrambot.model.NumberLesson;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.*;

@Log4j2
@Component
public class HandlerHelper {
    private final GroupService groupService;
    private final UserService userService;

    public HandlerHelper(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
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

    public static GroupOrTeacher getData(User user) {
        if (user == null)
            return null;
        switch (user.getRole()) {
            case STUDENT:
                return user.getGroup();
            case TEACHER:
                return user.getTeacher();
        }
        return null;
    }


    public static PartialBotApiMethod<? extends Serializable> mainMessage(User user) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        LocalDate nextSchoolDate = DateHelper.getNextSchoolDate();
        String weekType = DateHelper.getWeekType().equals(WeekType.DOWN) ? "нижняя" : "верхняя";
        String name = nextSchoolDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
        Role role = user.getRole();


        markup.setKeyboard(List.of(
                TelegramUtil.createKeyboardRow(MainCommand.getKeyAll())));
        return role.equals(Role.STUDENT) ?
                TelegramUtil.createMessageTemplate(user)
                        .setText("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tРасписание на эту неделю (" + weekType + ")\n" +
                                "3.\tУзнать расписание педагога\n" +
                                "4.\tИзменить анкету")
                        .setReplyMarkup(markup) :
                TelegramUtil.createMessageTemplate(user)
                        .setText("1.\tРасписание на " + "<b>" + nextSchoolDate + "</b>" + " (" + name + ")\n" +
                                "2.\tРасписание на эту неделю (" + weekType + ")\n" +
                                "3.\tУзнать расписание группы\n" +
                                "4.\tИзменить анкету")
                        .setReplyMarkup(markup);
    }

    public List<PartialBotApiMethod<? extends Serializable>> selectGroup(User user, String message) {
        if (Courses.containsKey(message)) {
            String s = Courses.get(message);
            Integer academicYear = TelegramUtil.getAcademicYear(s);
            int beginIndex = 2;
            String academicYearSuffix = academicYear.toString().substring(beginIndex);
            List<IdGroupName> groups = groupService.getAllGroupNameByYearSuffix(academicYearSuffix);


            user.setUserState(UserState.SELECT_GROUP);
            return List.of(
                    TelegramUtil.createMessageTemplate(userService.save(user))
                            .setText("Выбери группу...")
                            .setReplyMarkup(new InlineKeyboardMarkup()
                                    .setKeyboard(TelegramUtil.createInlineKeyboardButtons(groups, 3))
                            ));
        }
        return Collections.emptyList();
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
