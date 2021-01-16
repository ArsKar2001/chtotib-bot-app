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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.DAYS_OF_WEEK;
import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.MainCommand.*;

@Component
public class MainHandler implements Handler {

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

        stringBuilder.append("<b>ChatId</b>:\t").append(user.getChatId()).append("\n")
                .append("<b>Роль</b>:\t").append(Role.valueOf(user.getRoleName()).getValue()).append("\n");
        if (isStudent(user)) {
            Group group = groupRepository
                    .findById(user.getGroupId())
                    .orElseThrow(() -> new RuntimeException("не найден group по id " + user.getGroupId()));
            stringBuilder.append("<b>Группа</b>:\t").append(group.getGroupName()).append("\n");
        } else {
            stringBuilder.append("<b>Имя</b>:\t").append(user.getName()).append("\n");
        }
        stringBuilder.append("\n<b>От разработчика</b>:")
                .append("\nЭто не финальная версия моего бота. " +
                        "Сейчас он работает только с расписанием, однако в будущем будет учитывать замену и кидать новости с сайта <a href=\"https://www.chtotib.ru/\">ЧТОТиБ</a>. " +
                        "По всем ошибкам, вопросам или предложениям писать мне - @l_karmanchik_l.").append("\n");

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
        WeekType week = TelegramUtil.getWeekType();

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
        WeekType weekType = TelegramUtil.getWeekType();
        int nextDayOfWeek = TelegramUtil.getNextDayOfWeek();
        Date nextDay = TelegramUtil.getNextDate();
        StringBuilder stringBuilder = new StringBuilder();

        if (isStudent(user)) {
            Group group = groupRepository
                    .findById(user.getGroupId())
                    .orElseThrow(() -> new RuntimeException("не найден group по id " + user.getGroupId()));
            List<Lesson> lessonsForTomorrow = groupRepository
                    .findAllByGroupNameAndDayOfWeek(group.getGroupName(), nextDayOfWeek, weekType.name());
            String dayOfWeek = DAYS_OF_WEEK.get(nextDayOfWeek);
            stringBuilder.append("Расписание на <b>").append(nextDay.toString()).append("</b> (").append(dayOfWeek).append("):\n");
            lessonsForTomorrow.forEach(lesson -> stringBuilder.append("\t\t-\t").append(lesson.getLessonNumber()).
                    append("\t|\t").append(lesson.getDiscipline()).
                    append("\t|\t").append(lesson.getAuditorium()).
                    append("\t|\t").append(lesson.getTeacher())
                    .append("\n"));
            stringBuilder.append("--------------------------\n");
            stringBuilder.append("Группа: <b>").append(group.getGroupName()).append("</b>\t\t\t\t").append("Неделя: ").append("<b>").append(weekType.getValue()).append("</b>");
        } else {
            List<Lesson> lessonsForTomorrow = groupRepository.findAllByTeacherAndDayOfWeek(user.getName().toLowerCase(), nextDayOfWeek, weekType.name());
            String dayOfWeek = DAYS_OF_WEEK.get(nextDayOfWeek);
            stringBuilder.append("Расписание на <b>").append(nextDay.toString()).append("</b> (").append(dayOfWeek).append("):\n");

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
