package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.model.Lesson;
import com.karmanchik.chtotibtelegrambot.model.Role;
import com.karmanchik.chtotibtelegrambot.model.State;
import com.karmanchik.chtotibtelegrambot.model.WeekType;
import com.karmanchik.chtotibtelegrambot.repository.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.repository.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.DAYS_OF_WEEK;
import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.MainCommand.*;

@Component
public class MainHandler implements Handler {
    @Value("${bot.name}")
    private String botUsername;

    private final JpaGroupRepository groupRepository;
    private final JpaUserRepository userRepository;

    public MainHandler(JpaGroupRepository groupRepository,
                       JpaUserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            if (message.equalsIgnoreCase(COM_1.getValue())) {
                return getTimetableForTomorrow(user);
            } else if (message.equalsIgnoreCase(COM_2.getValue())) {
                return getFullTimetableForGroup(user);
            } else if (message.equalsIgnoreCase(COM_3.getValue())) {
                return getInformationOfBot(user);
            } else if (message.equalsIgnoreCase(COM_4.getValue())) {
                return getEditProfile(user);
            }
            return List.of(TelegramUtil.mainMessage(user));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(
                    TelegramUtil.createMessageTemplate(user)
                            .setText("Ошибка: " + e.getMessage()),
                    TelegramUtil.mainMessage(user)
            );
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> getInformationOfBot(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isStudent(user)) {
            Group group = groupRepository
                    .findById(user.getGroupId())
                    .orElseThrow(() -> new RuntimeException("не найден group по id " + user.getGroupId()));
            stringBuilder.append(String.format("<b>Твой данные</b>:%n"))
                    .append("\t\t<b>ChatId</b> - ").append(user.getChatId()).append("\n")
                    .append("\t\t<b>Роль</b> - ").append(user.getRoleName()).append("\n")
                    .append("\t\t<b>Группа</b> - ").append(group.getGroupName()).append("\n")
                    .append("<b>Бот</b>:" + "\n")
                    .append("\t\t<b>BotUserName</b> - ").append(botUsername).append("\n")
                    .append("<b>Разработчик</b>: ").append("@karmanchik999").append("\n");
        } else {
            stringBuilder.append(String.format("<b>Твой данные</b>:%n"))
                    .append("\t\t<b>chat_id</b> - ").append(user.getChatId()).append("\n")
                    .append("\t\t<b>Роль</b> - ").append(user.getRoleName()).append("\n")
                    .append("\t\t<b>Имя</b> - ").append(user.getName()).append("\n")
                    .append("<b>Бот</b>:" + "\n")
                    .append("\t\t<b>BotUserName</b> - ").append(botUsername).append("\n")
                    .append("<b>Разработчик</b>: ").append("@karmanchik999").append("\n");
        }

        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                TelegramUtil.mainMessage(user)
        );
    }

    private boolean isStudent(User user) {
        return user.getRoleName().equalsIgnoreCase(Role.STUDENT.name());
    }

    private List<PartialBotApiMethod<? extends Serializable>> getEditProfile(User user) {
        user.setRoleName(Role.NONE.name());
        user.setUserState(State.SELECT_ROLE);
        user.setBotState(State.REG);
        userRepository.save(user);
        return RegistrationHandler.selectRole(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> getFullTimetableForGroup(User user) {
        StringBuilder stringBuilder = new StringBuilder();
        WeekType week = getWeekType();

        if (isStudent(user)) {
            Group group = groupRepository
                    .findById(user.getGroupId())
                    .orElseThrow(() -> new RuntimeException("не найден group по id " + user.getGroupId()));
            List<Integer> dayList = groupRepository.getListDaysOfWeekByGroupName(group.getGroupName());
            List<Lesson> lessons = groupRepository.getListLessonByGroupNameAndWeekType(group.getGroupName(), week.name());

            stringBuilder.append("Расписание для группы ").append("<b>").append(group.getGroupName()).append("</b>").append(":\n");
            dayList.forEach(day -> {
                String dayOfWeek = DAYS_OF_WEEK.get(day);
                stringBuilder.append("--------------------------\n");
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
            });
        } else {
            List<Lesson> lessons = groupRepository.getListLessonByTeacherAndWeekType(user.getName().toLowerCase(), week.name());
            List<Integer> dayList = groupRepository.getListDaysOfWeekByTeacher(user.getName());
            stringBuilder.append("Расписание для педагога ").append("<b>").append(user.getName()).append("</b>").append(":\n");
            dayList.forEach(day -> {
                stringBuilder.append("--------------------------\n");
                stringBuilder.append(DAYS_OF_WEEK.get(day)).append(":\n");
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
            });
        }
        stringBuilder.append("--------------------------\n");
        stringBuilder.append("Неделя: ").append("<b>").append(week.getValue()).append("</b>");
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                TelegramUtil.mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableForTomorrow(User user) {
        WeekType weekType = getWeekType();
        int tomorrowDayOfWeek = getTomorrowDayOfWeek();
        Date tomorrow = getTomorrowDate();
        StringBuilder stringBuilder = new StringBuilder();

        if (isStudent(user)) {
            Group group = groupRepository
                    .findById(user.getGroupId())
                    .orElseThrow(() -> new RuntimeException("не найден group по id " + user.getGroupId()));
            List<Lesson> lessonsForTomorrow = groupRepository
                    .findAllByGroupNameAndDayOfWeek(group.getGroupName(), tomorrowDayOfWeek, weekType.name());
            String dayOfWeek = DAYS_OF_WEEK.get(lessonsForTomorrow.get(0).getDayOfWeek());
            stringBuilder.append("Расписание на ").append(tomorrow.toString()).append(" (").append(dayOfWeek).append("):\n");
            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                    append("\t|\t").append(lesson.getDiscipline()).
                    append("\t|\t").append(lesson.getAuditorium()).
                    append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append("--------------------------\n");
            stringBuilder.append("Группа: ").append(group.getGroupName()).append("\t\t\t\t").append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        } else {
            List<Lesson> lessonsForTomorrow = groupRepository.findAllByTeacherAndDayOfWeek(user.getName().toLowerCase(), tomorrowDayOfWeek, weekType.name());
            String dayOfWeek = DAYS_OF_WEEK.get(tomorrowDayOfWeek);
            stringBuilder.append("Расписание на ").append(tomorrow.toString()).append(" (").append(dayOfWeek).append("):\n");

            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                    append("\t|\t").append(lesson.getGroupName()).
                    append("\t|\t").append(lesson.getDiscipline()).
                    append("\t|\t").append(lesson.getAuditorium()).
                    append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append("--------------------------\n");
            stringBuilder.append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        }
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                TelegramUtil.mainMessage(user)
        );
    }

    private Date getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return new Date(calendar.getTimeInMillis());
    }

    private WeekType getWeekType() {
        return TelegramUtil.isEvenWeek() ? WeekType.UP : WeekType.DOWN;
    }

    private int getTomorrowDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int tomorrowDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return (tomorrowDayOfWeek >= 5) ? 0 : tomorrowDayOfWeek;
    }

    @Override
    public State operatedBotState() {
        return State.AUTHORIZED;
    }

    @Override
    public List<State> operatedUserListState() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
