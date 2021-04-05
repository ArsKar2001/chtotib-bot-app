package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.DateHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.jpa.models.GroupOrTeacher;
import com.karmanchik.chtotibtelegrambot.jpa.service.GroupService;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import com.karmanchik.chtotibtelegrambot.model.NumberLesson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.MESSAGE_SPLIT;

@Log4j2
@Component
@RequiredArgsConstructor
public class SelectTimetableGroupHandler implements Handler {
    private final HandlerHelper helper;
    private final GroupService groupService;
    private final UserService userService;

    /**
     * Обработчик входящих сообщений пользователя.
     *
     * @param user    пользователь
     * @param message сообщение
     * @return
     */
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) throws ResourceNotFoundException {
        switch (user.getUserState()) {
            case SELECT_COURSE:
                return helper.selectGroup(user, message);
            case SELECT_GROUP:
                return selectGroupOrAccept(user, message);
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectGroupOrAccept(User user, String message) throws ResourceNotFoundException {

        if (Courses.containsKey(message)) {
            return helper.selectGroup(user, message);
        } else if (HandlerHelper.isNumeric(message)) {
            int id = Integer.parseInt(message);
            log.info("Find group by id: {} ...", id);
            GroupOrTeacher group = groupService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(id, Group.class));
            return List.of(
                    getTimetable(user, group),
                    HandlerHelper.mainMessage(user)
            );
        }
        return Collections.emptyList();
    }

    private PartialBotApiMethod<? extends Serializable> getTimetable(User user, GroupOrTeacher groupOrTeacher) {
        WeekType weekType = DateHelper.getWeekType();
        StringBuilder message = new StringBuilder();
        user.setUserState(UserState.NONE);

        message.append("Расписание ").append("<b>").append(groupOrTeacher.getName()).append("</b>:").append("\n");
        groupOrTeacher.getLessons().stream()
                .map(Lesson::getDay)
                .distinct()
                .forEach(day -> {
                    String displayName = DayOfWeek.of(day).getDisplayName(TextStyle.FULL, DateHelper.getLocale());
                    message.append(MESSAGE_SPLIT).append("\n")
                            .append("<b>").append(displayName).append("</b>:").append("\n");
                    groupOrTeacher.getLessons().stream()
                            .filter(lesson -> lesson.getDay().equals(day))
                            .filter(lesson -> lesson.getWeekType() == weekType || lesson.getWeekType() == WeekType.NONE)
                            .forEach(lesson -> {
                                String number = NumberLesson.get(lesson.getPairNumber());
                                message.append("\t")
                                        .append(number).append("\t<b>|</b>\t")
                                        .append(lesson.getDiscipline()).append("\t<b>|</b>\t")
                                        .append(lesson.getAuditorium()).append("\t<b>|</b>\t")
                                        .append(lesson.getTeacherName())
                                        .append("\n");
                            });
                });
        return TelegramUtil.createMessageTemplate(userService.save(user))
                .setText(message.toString());
    }

    public static List<PartialBotApiMethod<? extends Serializable>> start(User user) {
        return TelegramUtil.createSelectCourseButtonPanel(user);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.SELECT_COURSE,
                UserState.SELECT_GROUP
        );
    }
}
