package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.helper.DateHelper;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelperService;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.Helper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaLessonsRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaChatUserRepository;
import com.karmanchik.chtotibtelegrambot.model.Course;
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
public class TimetableGroupHandler implements Handler {
    private final JpaChatUserRepository userRepository;
    private final HandlerHelperService helper;
    private final JpaGroupRepository groupRepository;
    private final JpaLessonsRepository lessonsRepository;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(ChatUser chatUser, String message) throws ResourceNotFoundException {
        return switch (chatUser.getUserState()) {
            case SELECT_COURSE -> helper.selectGroup(chatUser, message);
            case SELECT_GROUP -> selectGroupOrAccept(chatUser, message);
            default -> List.of(
                    cancel(chatUser)
            );
        };
    }

    public static List<PartialBotApiMethod<? extends Serializable>> start(ChatUser chatUser) {
        return TelegramUtil.createSelectCourseButtonPanel(chatUser);
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectGroupOrAccept(ChatUser chatUser, String message) throws ResourceNotFoundException {

        if (Course.isCourse(message)) {
            return helper.selectGroup(chatUser, message);
        } else if (HandlerHelperService.isNumeric(message)) {
            int id = Integer.parseInt(message);
            log.info("Find group by id: {} ...", id);
            Group group = groupRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(id, Group.class));
            chatUser.setGroup(group);
            return List.of(
                    createMessage(chatUser, group),
                    cancel(chatUser)
            );
        }
        return Collections.emptyList();
    }

    private PartialBotApiMethod<? extends Serializable> createMessage(ChatUser chatUser, Group group) {
        WeekType weekType = DateHelper.getWeekType();
        StringBuilder message = new StringBuilder();

        List<Lesson> lessons = lessonsRepository.findByGroup(group);
        message.append("Расписание ").append("<b>").append(group.getName()).append("</b>:").append("\n");
        lessons.stream()
                .map(Lesson::getDay)
                .distinct()
                .sorted()
                .forEach(day -> {
                    String displayName = DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Helper.getLocale());
                    message.append(MESSAGE_SPLIT).append("\n")
                            .append("<b>").append(displayName).append("</b>:").append("\n");
                    lessons.stream()
                            .filter(lesson -> lesson.getDay().equals(day))
                            .filter(lesson -> lesson.getWeekType() == weekType || lesson.getWeekType() == WeekType.NONE)
                            .forEach(Helper.getLessonGroup(message));
                });
        return TelegramUtil.createMessageTemplate(chatUser)
                .text(message.toString()).build();
    }

    private PartialBotApiMethod<? extends Serializable> cancel(ChatUser chatUser) {
        chatUser.setUserState(UserState.NONE);
        return HandlerHelperService.mainMessage(userRepository.save(chatUser));
    }

    @Override
    public BotState operatedBotState() {
        return BotState.AUTHORIZED;
    }

    @Override
    public List<Role> operatedUserRoles() {
        return List.of(Role.TEACHER);
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.SELECT_COURSE,
                UserState.SELECT_GROUP
        );
    }
}
