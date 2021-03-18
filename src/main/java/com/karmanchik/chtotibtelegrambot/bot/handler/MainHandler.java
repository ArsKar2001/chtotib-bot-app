package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.*;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.model.DayOfWeek;
import com.karmanchik.chtotibtelegrambot.model.WeekType;
import com.karmanchik.chtotibtelegrambot.service.GroupService;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainHandler implements Handler {

    private final GroupService groupService;
    private final UserService userService;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            if (Command.isCommand(message)) {
                final Command command = Command.valueOfKey(message);
                switch (command) {
                    case COM_1:
                        log.debug("!!!! log debug 1: select handler - getTimetableNextDay for {}", user.toString());
                        return this.getTimetableNextDay(user);
                    case COM_2:
                        log.debug("!!!! log debug 1: select handler - getFullTimetable for {}", user.toString());
                        return this.getFullTimetable(user);
                    case COM_3:
                        log.debug("!!!! log debug 1: select handler - getMessageInfo for {}", user.toString());
                        return this.getMessageInfo(user);
                    case COM_4:
                        log.debug("!!!! log debug 1: select handler - getEditProfile for {}", user.toString());
                        return this.getEditProfile(user);
                    default:
                        log.warn("Unsupported command {} for user {}", command, user.getId());
                        return Collections.emptyList();
                }
            }
            return List.of(mainMessage(user));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return List.of(
                    createMessageTemplate(user)
                            .setText("Ошибка: " + e.getMessage() + ";" + e.getLocalizedMessage()),
                    mainMessage(user)
            );
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> getMessageInfo(User user) {
        StringBuilder stringBuilder = new StringBuilder();

        if (userService.isStudent(user))
            stringBuilder.append("<b>Группа</b>:\t").append(user.getGroup().getGroupName()).append("\n");
        else stringBuilder.append("<b>Имя</b>:\t").append(user.getName()).append("\n");
        stringBuilder.append("\n<b>От разработчика</b>:")
                .append("\nЭто не финальная версия моего бота. " +
                        "Сейчас он работает только с расписанием, однако в будущем будет учитывать замену и кидать новости с сайта <a href=\"https://www.chtotib.ru/\">ЧТОТиБ</a>. " +
                        "По всем ошибкам, вопросам или предложениям писать мне - @l_karmanchik_l.").append("\n");

        return List.of(
                createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> getEditProfile(User user) {
        user.setRoleId(Constants.Role.NONE);
        user.setGroupId(Constants.Group.NONE);
        user.setTeacher("");
        user.setUserStateId(Constants.UserState.SELECT_ROLE);
        user.setBotStateId(Constants.BotState.REG);
        final User saveUser = userService.save(user);
        return RegistrationHandler.selectRole(saveUser);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getFullTimetable(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        log.debug("!!!! log debug getFullTimetable: create {}", stringBuilder.getClass().toString());
        Calendar calendar = Calendar.getInstance();
        log.debug("!!!! log debug getFullTimetable: create {}", calendar.getClass().toString() + " - " + calendar.toString());
        WeekType week = getWeekType(calendar);
        log.debug("!!!! log debug getFullTimetable: create {}", week.getClass().toString() + " - " + week.toString());

        if (userService.isStudent(user)) {
            Group group = user.getGroup();
            log.debug("!!!! log debug getFullTimetable: find group by id=" + group.getId() + " - " + group.toString());
            var dayList = groupService.findAllDaysOfWeekByGroupName(group.getGroupName());
            log.debug("!!!! log debug getFullTimetable: find dayList by " + group.getGroupName() + " - " + Arrays.toString(dayList.toArray()));
            var lessons = groupService.findAllLessonsByGroup(group.getGroupName(), week.name());
            log.debug("!!!! log debug getFullTimetable: find lessons by " + group.getGroupName() + " - " + Arrays.toString(lessons.toArray()));

            stringBuilder.append("Неделя: ").append("<b>").append(week.getValue()).append("</b>").append("\n");
            stringBuilder.append("Расписание для группы ").append("<b>").append(group.getGroupName()).append("</b>").append(":\n");
            stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            dayList.forEach(day -> {
                String dayOfWeek = DayOfWeek.get(day);
                stringBuilder.append(dayOfWeek).append(":\n");
                lessons.forEach(lesson -> {
                    if (lesson.getDayOfWeek().equals(day)) {
                        stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                                append("\t|\t").append(lesson.getDiscipline()).
                                append("\t|\t").append(lesson.getAuditorium()).
                                append("\t|\t").append(lesson.getTeacher())
                                .append("\n");
                    }
                });
                stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            });
        } else if (userService.isTeacher(user)) {
            var lessons = groupService.findAllLessonsByTeacher(user.getName().toLowerCase(), week.name());
            log.debug("!!!! log debug getFullTimetable: find lessons by " + user.getName() + " - " + Arrays.toString(lessons.toArray()));
            var dayList = groupService.findAllDaysOfWeekByTeacher(user.getName());
            log.debug("!!!! log debug getFullTimetable: find dayList by " + user.getName() + " - " + Arrays.toString(dayList.toArray()));
            stringBuilder.append("Неделя: ").append("<b>").append(week.getValue()).append("</b>").append("\n");
            stringBuilder.append("Расписание для педагога ").append("<b>").append(user.getName()).append("</b>").append(":\n");
            stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            dayList.forEach(day -> {
                stringBuilder.append(DayOfWeek.get(day)).append(":\n");
                lessons.forEach(lesson -> {
                    if (lesson.getDayOfWeek().equals(day)) {
                        stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                                append("\t|\t").append(lesson.getGroupName()).
                                append("\t|\t").append(lesson.getDiscipline()).
                                append("\t|\t").append(lesson.getAuditorium()).
                                append("\t|\t").append(lesson.getTeacher())
                                .append("\n");
                    }
                });
                stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            });
        }
        return List.of(
                createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(User user) {
        Calendar next = getNextDate();
        log.debug("!!!! log debug getTimetableNextDay: create Calendar - " + next.toString());

        int nextDayOfWeek = getNextDayOfWeek();
        log.debug("!!!! log debug getTimetableNextDay: create nextDayOfWeek - " + nextDayOfWeek);
        WeekType weekType = getWeekType(next);
        log.debug("!!!! log debug getTimetableNextDay: create weekType - " + weekType.toString());

        StringBuilder stringBuilder = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        if (userService.isStudent(user)) {
            Group group = user.getGroup();
            log.debug("!!!! log debug getTimetableNextDay: find group by id=" + group.getId() + " - " + group.toString());
            var lessonsForTomorrow = groupService.findAllByGroupNameAndDayOfWeek(group.getGroupName(), nextDayOfWeek, weekType.name());
            log.debug("!!!! log debug getTimetableNextDay: find lessons by " + group.getGroupName() + " - " + Arrays.toString(lessonsForTomorrow.toArray()));
            String dayOfWeek = DayOfWeek.get(nextDayOfWeek);
            stringBuilder.append("Расписание на <b>").append(dateFormat.format(next.getTime())).append("</b> (").append(dayOfWeek).append("):\n");
            stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t")
                    .append(lesson.getLessonNumber()).
                            append("\t|\t").append(lesson.getDiscipline()).
                            append("\t|\t").append(lesson.getAuditorium()).
                            append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            stringBuilder.append("Группа: <b>").append(group.getGroupName()).append("</b>\t\t\t\t").append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        } else {
            var lessonsForTomorrow = groupService.findAllByTeacherAndDayOfWeek(user.getName().toLowerCase(), nextDayOfWeek, weekType.name());
            log.debug("!!!! log debug getTimetableNextDay: find lessons by " + user.getName() + " - " + Arrays.toString(lessonsForTomorrow.toArray()));
            String dayOfWeek = DayOfWeek.get(nextDayOfWeek);
            stringBuilder.append("Расписание на <b>").append(dateFormat.format(next.getTime())).append("</b> (").append(dayOfWeek).append("):\n");
            stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                    append("\t|\t").append(lesson.getGroupName()).
                    append("\t|\t").append(lesson.getDiscipline()).
                    append("\t|\t").append(lesson.getAuditorium()).
                    append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append(new String(new char[60]).replace('\0', '-')).append("\n");
            stringBuilder.append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        }
        return List.of(
                createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                mainMessage(user)
        );
    }

    @Override
    public Integer operatedBotStateId() {
        return Constants.BotState.AUTHORIZED;
    }

    @Override
    public List<Integer> operatedUserListStateId() {
        return List.of(Constants.UserState.NONE);
    }

    public enum Command {
        COM_1,
        COM_2,
        COM_3,
        COM_4;
        public static final Map<String, Command> COMMAND_MAP = new HashMap<>();

        private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\d+");

        static {
            for (var value : values()) {
                int key = value.ordinal() + 1;
                COMMAND_MAP.put(String.valueOf(key), value);
            }
        }

        public static Command valueOfKey(String s) {
            return COMMAND_MAP.get(s);
        }

        public static boolean isCommand(String s) {
            return COMMAND_PATTERN.matcher(s).matches();
        }
    }
}
