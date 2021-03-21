package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.model.DayOfWeek;
import com.karmanchik.chtotibtelegrambot.model.WeekType;
import com.karmanchik.chtotibtelegrambot.service.GroupService;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.karmanchik.chtotibtelegrambot.entity.constants.Constants.User.ENTER_NAME;
import static com.karmanchik.chtotibtelegrambot.entity.constants.Constants.User.NONE;
import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class SelectTimetableHandler implements Handler {

    private final UserService userService;
    private final GroupService groupService;
    private final Helper helper;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (Command.isCommand(message)) {
            Command command = Command.valueByKey(message);
            switch (command) {
                case TIMETABLE_TOMORROW:
                    log.debug("!!!! log debug 1: select handler - getTimetableNextDay for {}", user.toString());
                    return getTimetableNextDay(user);
                case TIMETABLE_FULL:
                    log.debug("!!!! log debug 1: select handler - getFullTimetable for {}", user.toString());
                    return getFullTimetable(user);
                case TIMETABLE_TEACHER:
                    log.debug("!!!! log debug 1: select handler - getTimetableByTeacher for {}", user.toString());
                    return getTimetableByTeacher(user);
                case TIMETABLE_GROUP:
                    log.debug("!!!! log debug 1: select handler - getTimetableByGroup for {}", user.toString());
                    return getTimetableByGroup(user);
                case CANCEL:
                    return cancel(user);
            }
        } else if (user.getUserStateId().equals(ENTER_NAME)) {
            return helper.createSelectTeacherButtonsPanel(user, message);
        }
        return null;
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableByGroup(User user) {
        return null;
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableByTeacher(User user) {
        return helper.inputTeacherName(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(User user) {
        Calendar next = getNextDate();
        log.debug("!!!! log debug getTimetableNextDay: create Calendar - " + next.toString());

        int nextDayOfWeek = getNextDayOfWeek();
        log.debug("!!!! log debug getTimetableNextDay: create nextDayOfWeek - " + nextDayOfWeek);
        WeekType weekType = getWeekType(next);
        log.debug("!!!! log debug getTimetableNextDay: create weekType - " + weekType.toString());

        StringBuilder stringBuilder = new StringBuilder();
        String spliterator = new String(new char[60]).replace('\0', '-');
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        if (userService.isStudent(user)) {
            Group group = user.getGroup();
            log.debug("!!!! log debug getTimetableNextDay: find group by id=" + group.getId() + " - " + group.toString());
            var lessonsForTomorrow = groupService.findAllByGroupNameAndDayOfWeek(group.getGroupName(), nextDayOfWeek, weekType.name());
            log.debug("!!!! log debug getTimetableNextDay: find lessons by " + group.getGroupName() + " - " + Arrays.toString(lessonsForTomorrow.toArray()));
            String dayOfWeek = DayOfWeek.get(nextDayOfWeek);
            stringBuilder.append("Расписание на <b>").append(dateFormat.format(next.getTime())).append("</b> (").append(dayOfWeek).append("):\n");
            stringBuilder.append(spliterator).append("\n");
            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t")
                    .append(lesson.getLessonNumber()).
                            append("\t|\t").append(lesson.getDiscipline()).
                            append("\t|\t").append(lesson.getAuditorium()).
                            append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append(spliterator).append("\n");
            stringBuilder.append("Группа: <b>").append(group.getGroupName()).append("</b>\t\t\t\t").append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        } else {
            var lessonsForTomorrow = groupService.findAllByTeacherAndDayOfWeek(user.getTeacher().toLowerCase(), nextDayOfWeek, weekType.name());
            log.debug("!!!! log debug getTimetableNextDay: find lessons by " + user.getTeacher() + " - " + Arrays.toString(lessonsForTomorrow.toArray()));
            String dayOfWeek = DayOfWeek.get(nextDayOfWeek);
            stringBuilder.append("Расписание на <b>").append(dateFormat.format(next.getTime())).append("</b> (").append(dayOfWeek).append("):\n");
            stringBuilder.append(spliterator).append("\n");
            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                    append("\t|\t").append(lesson.getGroupName()).
                    append("\t|\t").append(lesson.getDiscipline()).
                    append("\t|\t").append(lesson.getAuditorium()).
                    append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append(spliterator).append("\n");
            stringBuilder.append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        }
        return List.of(
                createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                Helper.mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> getFullTimetable(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        String spliterator = new String(new char[60]).replace('\0', '-');

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

            stringBuilder.append("Неделя: ").append("<b>").append(week.getValue()).append("</b>").append("\n");
            stringBuilder.append("Расписание для группы ").append("<b>").append(group.getGroupName()).append("</b>").append(":\n");
            stringBuilder.append(spliterator).append("\n");

            dayList.forEach(day -> {
                String dayOfWeek = DayOfWeek.get(day);
                stringBuilder.append(dayOfWeek).append(":\n");
                new JSONArray(group.getLessons()).forEach(item -> {
                    Lesson lesson = (Lesson) item;
                    if (lesson.getDayOfWeek().equals(day)) {
                        stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                                append("\t|\t").append(lesson.getDiscipline()).
                                append("\t|\t").append(lesson.getAuditorium()).
                                append("\t|\t").append(lesson.getTeacher())
                                .append("\n");
                    }
                });
                stringBuilder.append(spliterator).append("\n");
            });
        } else if (userService.isTeacher(user)) {
            String teacher = user.getTeacher();
            var lessons = groupService.findAllLessonsByTeacher(teacher.toLowerCase(), week.name());
            log.debug("!!!! log debug getFullTimetable: find lessons by " + teacher + " - " + Arrays.toString(lessons.toArray()));
            var dayList = groupService.findAllDaysOfWeekByTeacher(teacher);
            log.debug("!!!! log debug getFullTimetable: find dayList by " + teacher + " - " + Arrays.toString(dayList.toArray()));
            stringBuilder.append("Неделя: ").append("<b>").append(week.getValue()).append("</b>").append("\n");
            stringBuilder.append("Расписание для педагога ").append("<b>").append(teacher).append("</b>").append(":\n");
            stringBuilder.append(spliterator).append("\n");
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
                stringBuilder.append(spliterator).append("\n");
            });
        }
        return List.of(
                createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                Helper.mainMessage(user)
        );
    }

    public List<PartialBotApiMethod<? extends Serializable>> cancel(User user) {
        user.setUserStateId(NONE);
        User save = userService.save(user);
        return List.of(
                Helper.mainMessage(save)
        );
    }

    @Override
    public Integer operatedBotStateId() {
        return Constants.Bot.AUTHORIZED;
    }

    @Override
    public List<Integer> operatedUserListStateId() {
        return List.of(
                Constants.User.SELECT_TIMETABLE,
                ENTER_NAME);
    }

    private enum Command {
        TIMETABLE_TOMORROW,
        TIMETABLE_FULL,
        TIMETABLE_TEACHER,
        TIMETABLE_GROUP,
        CANCEL;

        public static final Map<String, Command> COMMAND_MAP = new HashMap<>();

        private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\d+");

        static {
            for (var value : values()) {
                int key = value.ordinal() + 1;
                COMMAND_MAP.put(String.valueOf(key), value);
            }
        }

        public static Command valueByKey(String s) {
            return COMMAND_MAP.get(s);
        }

        public static boolean isCommand(String s) {
            return COMMAND_PATTERN.matcher(s).matches();
        }
    }
}
