package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.DateHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper.MainCommand;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.entity.models.GroupOrTeacher;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.model.NumberLesson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.MESSAGE_SPLIT;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainHandler implements Handler {
    private final JpaUserRepository userRepository;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (isCommand(message)) {
            MainCommand command = MainCommand.get(message);
            switch (command) {
                case COMMAND_1:
                    return getTimetableNextDay(user);
                case COMMAND_2:
                    return getTimetableFull(user);
                case COMMAND_3:
                    return getTimetableOther(user);
                case COMMAND_4:
                    return editProfile(user);
            }
        }
        return List.of(HandlerHelper.mainMessage(user));
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableOther(User user) {
        Role role = user.getRole();
        if (role.equals(Role.STUDENT)) {
            user.setUserState(UserState.INPUT_TEXT);
            return TimetableTeacherHandler.start(userRepository.save(user));
        } else {
            user.setUserState(UserState.SELECT_COURSE);
            return TimetableGroupHandler.start(userRepository.save(user));
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableFull(User user) {
        WeekType weekType = DateHelper.getWeekType();
        StringBuilder message = new StringBuilder();
        GroupOrTeacher data = HandlerHelper.getData(user);
        Role role = user.getRole();
        message.append("Расписание ").append("<b>").append(data.getName()).append("</b>:").append("\n");
        data.getLessons().stream()
                .map(Lesson::getDay)
                .distinct()
                .forEach(day -> {
                    String displayName = DayOfWeek.of(day).getDisplayName(TextStyle.FULL, DateHelper.getLocale());
                    message.append(MESSAGE_SPLIT).append("\n")
                            .append("<b>").append(displayName).append("</b>:").append("\n");
                    data.getLessons().stream()
                            .filter(lesson -> lesson.getDay().equals(day))
                            .filter(lesson -> lesson.getWeekType() == weekType || lesson.getWeekType() == WeekType.NONE)
                            .forEach(lesson -> {
                                String number = NumberLesson.get(lesson.getPairNumber());
                                message.append("\t")
                                        .append(number).append("\t<b>|</b>\t")
                                        .append(lesson.getDiscipline()).append("\t<b>|</b>\t")
                                        .append(lesson.getAuditorium()).append("\t<b>|</b>\t");
                                HandlerHelper.setGroupOrTeachers(role, message, lesson);
                            });
                });
        return List.of(TelegramUtil.createMessageTemplate(user)
                        .setText(message.toString()),
                HandlerHelper.mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(User user) {
        GroupOrTeacher data = HandlerHelper.getData(user);
        LocalDate date = DateHelper.getNextSchoolDate();
        WeekType weekType = DateHelper.getWeekType();
        String name = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
        Role role = user.getRole();


        StringBuilder message = new StringBuilder()
                .append("Расписание ").append("<b>").append(data.getName()).append("</b>").append(" на ").append("<b>").append(date).append("</b>").append(" (").append(name).append("):").append("\n")
                .append(MESSAGE_SPLIT).append("\n");
        data.getLessons().stream()
                .filter(lesson -> lesson.getDay() == date.getDayOfWeek().getValue())
                .filter(lesson -> lesson.getWeekType() == weekType || lesson.getWeekType() == WeekType.NONE)
                .forEach(lesson -> {
                    String number = NumberLesson.get(lesson.getPairNumber());
                    message.append("\t")
                            .append(number).append("\t<b>|</b>\t")
                            .append(lesson.getDiscipline()).append("\t<b>|</b>\t")
                            .append(lesson.getAuditorium()).append("\t<b>|</b>\t");
                    HandlerHelper.setGroupOrTeachers(role, message, lesson);
                });
        if (!data.getReplacements().isEmpty()) {
            message.append(MESSAGE_SPLIT).append("\n")
                    .append("Замена:").append("\n")
                    .append(MESSAGE_SPLIT).append("\n");
            data.getReplacements().forEach(replacement -> {
                message.append("\t")
                        .append(replacement.getDate()).append("\t<b>|</b>\t")
                        .append(replacement.getPairNumber()).append("\t<b>|</b>\t")
                        .append(replacement.getDiscipline()).append("\t<b>|</b>\t")
                        .append(replacement.getAuditorium()).append("\t<b>|</b>\t");
                if (role.equals(Role.STUDENT)) {
                    replacement.getTeachers()
                            .forEach(t -> message.append(t.getName())
                                    .append(" "));
                } else {
                    message.append(replacement.getGroup().getName());
                }
                message.append("\n");
            });
        }
        return List.of(TelegramUtil.createMessageTemplate(user)
                        .setText(message.toString()),
                HandlerHelper.mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> editProfile(User user) {
        user.setBotState(BotState.REG);
        user.setUserState(UserState.SELECT_ROLE);
        return List.of(
                HandlerHelper.selectRole(userRepository.save(user)));
    }

    private boolean isCommand(String message) {
        return MainCommand.getMap().containsKey(message);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.NONE,
                UserState.INPUT_TEXT
        );
    }
}
