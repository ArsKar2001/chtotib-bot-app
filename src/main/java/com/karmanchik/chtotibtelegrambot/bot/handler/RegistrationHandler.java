package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.model.Courses;
import com.karmanchik.chtotibtelegrambot.service.GroupService;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.Month;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.karmanchik.chtotibtelegrambot.bot.handler.Helper.*;
import static java.lang.Integer.parseInt;

@Log4j2
@Component
@RequiredArgsConstructor
public class RegistrationHandler implements Handler {

    private static final String ROLE_STUDENT = "студент";
    private static final String ROLE_TEACHER = "педагог";

    private final UserService userService;
    private final GroupService groupService;
    private final Helper helper;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            switch (user.getUserState().getCode()) {
                case "SELECT_ROLE":
                    return switchRole(user, message);
                case "SELECT_COURSE":
                    return helper.selectGroup(user, message);
                case "SELECT_GROUP":
                    return selectOrAccept(user, message);
                case "SELECT_OPTION":
                    if (message.equalsIgnoreCase(ACCEPT))
                        return helper.accept(user);
                    if (message.equalsIgnoreCase(CHANGE))
                        return helper.cancel(user);
                case "ENTER_NAME":
                    return helper.createSelectTeacherButtonsPanel(user, message);
                case "SELECT_TEACHER":
                    return selectTeacher(user, message);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return List.of(
                    TelegramUtil.createMessageTemplate(user)
                            .setText("<b>Ошибка</b>: " + e.getMessage())
            );
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectOrAccept(User user, String message) {
        if (Courses.containsKey(message)) {
            return helper.selectGroup(user, message);
        } else if (groupService.isGroupId(message)) {
            user.setGroupId(parseInt(message));
            final User saveUser = userService.save(user);
            return helper.accept(saveUser);
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> selectTeacher(User user, String message) {
        List<String> allTeachers = groupService.findAllTeachers();
        if (message.equalsIgnoreCase(CANCEL)) {
            user.setUserStateId(Constants.User.ENTER_NAME);
            final User saveUser1 = userService.save(user);
            return helper.inputTeacherName(saveUser1);
        } else if (allTeachers.contains(message)) {
            user.setTeacher(message);
            final User saveUser2 = userService.save(user);
            return helper.accept(saveUser2);
        } else {
            return helper.createMessageDidNotDefine(user);
        }
    }

    static List<PartialBotApiMethod<? extends Serializable>> selectRole(User user) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(
                ROLE_STUDENT.toUpperCase(),
                ROLE_TEACHER.toUpperCase()
        ));
        markup.setKeyboard(List.of(keyboardRow));
        markup.setOneTimeKeyboard(true);

        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Кто ты?")
                        .setReplyMarkup(markup));
    }

    List<PartialBotApiMethod<? extends Serializable>> switchRole(User user, String message) {
        if (message.equalsIgnoreCase(ROLE_STUDENT)) {
            user.setUserStateId(Constants.User.SELECT_COURSE);
            user.setRoleId(Constants.Role.STUDENT);
            final User saveUser1 = userService.save(user);
            return helper.createSelectCourseButtonsPanel(saveUser1);
        } else if (message.equalsIgnoreCase(ROLE_TEACHER)) {
            user.setRoleId(Constants.Role.TEACHER);
            final User saveUser2 = userService.save(user);
            return helper.inputTeacherName(saveUser2);
        }
        return Collections.emptyList();
    }

    @Override
    public Integer operatedBotStateId() {
        return Constants.Bot.REG;
    }

    @Override
    public List<Integer> operatedUserListStateId() {
        return List.of(
                Constants.User.SELECT_COURSE,
                Constants.User.SELECT_ROLE,
                Constants.User.SELECT_GROUP,
                Constants.User.ENTER_NAME,
                Constants.User.SELECT_TEACHER
        );
    }
}
