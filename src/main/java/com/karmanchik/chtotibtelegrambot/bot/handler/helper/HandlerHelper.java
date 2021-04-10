package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.entity.models.GroupOrTeacher;
import com.karmanchik.chtotibtelegrambot.entity.models.IdGroupName;
import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaLessonsRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaTeacherRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_STUDENT;
import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.ROLE_TEACHER;

@Log4j2
@Component
@RequiredArgsConstructor
public class HandlerHelper {
    private final JpaGroupRepository groupRepository;
    private final JpaUserRepository userRepository;
    private final JpaTeacherRepository teacherRepository;
    private final JpaLessonsRepository lessonsRepository;


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

    public List<Lesson> getLessonsByUser(User user) {
        if (user == null)
            return null;
        switch (user.getRole()) {
            case STUDENT:
                return groupRepository.findByUser(user)
                        .map(lessonsRepository::findByGroupOrderByDayAscPairNumberAsc)
                        .orElseThrow();
            case TEACHER:
                return teacherRepository.findByUser(user)
                        .map(lessonsRepository::findByTeacherOrderByDayAscPairNumberAsc)
                        .orElseThrow();
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
            List<IdGroupName> groups = groupRepository.getAllGroupNameByYearSuffix(academicYearSuffix);


            user.setUserState(UserState.SELECT_GROUP);
            return List.of(
                    TelegramUtil.createMessageTemplate(userRepository.save(user))
                            .setText("Выбери группу...")
                            .setReplyMarkup(new InlineKeyboardMarkup()
                                    .setKeyboard(TelegramUtil.createInlineKeyboardButtons(groups, 3))
                            ));
        }
        return Collections.emptyList();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum != null) try {
            Integer.parseInt(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
        else return false;
    }

    public static void setGroupOrTeachers(Role role, StringBuilder message, Lesson lesson) {
        if (role.equals(Role.STUDENT)) {
            lesson.getTeachers()
                    .forEach(t -> message.append(t.getName())
                            .append(" "));
        } else {
            message.append(lesson.getGroup().getName());
        }
        message.append("\n");
    }

    public GroupOrTeacher getData(User user) {
        if (user == null)
            return null;
        switch (user.getRole()) {
            case STUDENT:
                Group group = groupRepository.findByUser(user)
                        .orElseThrow();
                group.setLessons(
                        lessonsRepository.findByGroupOrderByDayAscPairNumberAsc(group)
                );
                return group;
            case TEACHER:
                Teacher teacher = teacherRepository.findByUser(user)
                        .orElseThrow();
                teacher.setLessons(lessonsRepository.findByTeacherOrderByDayAscPairNumberAsc(teacher));
                return teacher;
        }
        return null;
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
