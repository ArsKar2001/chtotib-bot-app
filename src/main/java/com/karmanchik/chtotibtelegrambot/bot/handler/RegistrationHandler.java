package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.Helper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import com.karmanchik.chtotibtelegrambot.jpa.enums.BotState;
import com.karmanchik.chtotibtelegrambot.jpa.enums.Role;
import com.karmanchik.chtotibtelegrambot.jpa.enums.UserState;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdTeacherName;
import com.karmanchik.chtotibtelegrambot.jpa.service.GroupService;
import com.karmanchik.chtotibtelegrambot.jpa.service.TeacherService;
import com.karmanchik.chtotibtelegrambot.jpa.service.UserService;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class RegistrationHandler implements Handler {

    private final UserService userService;
    private final GroupService groupService;
    private final TeacherService teacherService;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            UserState state = user.getUserState();
            switch (state) {
                case SELECT_COURSE:
                    return selectGroup(user, message);
                case SELECT_GROUP:
                    return selectGroupOrAccept(user, message);
                case SELECT_ROLE:
                    return switchRole(user, message);
                case ENTER_NAME:
                    return selectTeacher(user, message);
                case SELECT_TEACHER:
                    return selectTeacherOrAccept(user, message);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return List.of(
                    TelegramUtil.createMessageTemplate(user)
                            .setText("<b>Ошибка</b>: " + e.getMessage()));
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> switchRole(User user, String message) {
        if (message.equalsIgnoreCase(ROLE_STUDENT)) {
            user.setUserState(UserState.SELECT_COURSE);
            user.setRole(Role.STUDENT);
            User save = userService.save(user);
            return TelegramUtil.createSelectGroupButtonPanel(save);
        } else if (message.equalsIgnoreCase(ROLE_TEACHER)) {
            user.setRole(Role.TEACHER);
            return inputTeacherName(user);
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> inputTeacherName(User user) {
        user.setUserState(UserState.ENTER_NAME);
        User save = userService.save(user);
        return List.of(
                TelegramUtil.createMessageTemplate(save)
                        .setText("Введите фамилию...")
                        .enableMarkdown(false));
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectGroup(User user, String message) {
        if (Courses.containsKey(message)) {
            String s = Courses.get(message);
            Integer academicYear = TelegramUtil.getAcademicYear(s);
            int beginIndex = 2;
            String academicYearSuffix = academicYear.toString().substring(beginIndex);

            List<IdGroupName> groups = groupService.getAllGroupNameByYearSuffix(academicYearSuffix);

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(TelegramUtil.createInlineKeyboardButtons(groups, 3));

            user.setUserState(UserState.SELECT_GROUP);
            User save = userService.save(user);
            return List.of(
                    TelegramUtil.createMessageTemplate(save)
                            .setText("Выбери группу...")
                            .setReplyMarkup(markup));
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectGroupOrAccept(User user, String message) throws ResourceNotFoundException {

        if (Courses.containsKey(message)) {
            return selectGroup(user, message);
        } else if (Helper.isNumeric(message)) {
            int id = Integer.parseInt(message);
            log.info("Find group by id: {} ...", id);
            Group group = groupService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(id, Group.class));
            user.setGroup(group);
            return List.of(
                    accept(user)
            );
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectTeacherOrAccept(User user, String message) throws ResourceNotFoundException {
        if (message.equalsIgnoreCase(CANCEL)) {
            return inputTeacherName(user);
        } else if (Helper.isNumeric(message)) {
            int id = Integer.parseInt(message);
            Teacher teacher = teacherService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(id, Teacher.class));
            user.setTeacher(teacher);
            return List.of(
                    accept(user)
            );
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectTeacher(User user, String message) {
        List<IdTeacherName> teacherNames = teacherService.getAllIdTeacherNameByName(message);
        if (!teacherNames.isEmpty()) {
            user.setUserState(UserState.SELECT_TEACHER);
            User save = userService.save(user);

            final InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
            markup1.setKeyboard(TelegramUtil.createInlineKeyboardButtons(teacherNames, 2));

            final KeyboardRow row = TelegramUtil.createKeyboardRow(List.of(ConstantsHandler.CANCEL));
            final ReplyKeyboardMarkup markup2 = new ReplyKeyboardMarkup();
            markup2.setKeyboard(List.of(row));

            return sendMessageIsIsYou(save, markup1, markup2);
        }
        return sendMessageDidNotDefine(user);
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMessageIsIsYou(User user,
                                                                                 InlineKeyboardMarkup markup1,
                                                                                 ReplyKeyboardMarkup markup2) {
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Это вы...")
                        .setReplyMarkup(markup1),
                TelegramUtil.createMessageTemplate(user)
                        .setText("...?")
                        .setReplyMarkup(markup2));
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMessageDidNotDefine(User user) {
        String outMessage = "Не смог вас определить :(";
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(outMessage)
                        .enableMarkdown(false),
                cancel(user)
        );
    }

    private PartialBotApiMethod<? extends Serializable> accept(User user) {
        user.setUserState(UserState.NONE);
        user.setBotState(BotState.AUTHORIZED);
        User save = userService.save(user);
        return Helper.mainMessage(save);
    }

    private PartialBotApiMethod<? extends Serializable> cancel(User user) {
        user.setUserState(UserState.SELECT_ROLE);
        user.setBotState(BotState.REG);
        final User saveUser = userService.save(user);
        return Helper.selectRole(saveUser);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.REG;
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.SELECT_ROLE,
                UserState.SELECT_COURSE,
                UserState.SELECT_TEACHER,
                UserState.SELECT_GROUP,
                UserState.ENTER_NAME
        );
    }
}
