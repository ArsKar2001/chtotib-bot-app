package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.bot.handler.constants.ConstantsHandler;
import com.karmanchik.chtotibtelegrambot.bot.handler.helper.HandlerHelper;
import com.karmanchik.chtotibtelegrambot.bot.util.TelegramUtil;
import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.karmanchik.chtotibtelegrambot.model.GroupOrTeacher;
import com.karmanchik.chtotibtelegrambot.exception.ResourceNotFoundException;
import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaTeacherRepository;
import com.karmanchik.chtotibtelegrambot.jpa.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.model.Course;
import com.karmanchik.chtotibtelegrambot.model.IdTeacherName;
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

    private final JpaUserRepository userRepository;
    private final JpaGroupRepository groupRepository;
    private final JpaTeacherRepository teacherRepository;

    private final HandlerHelper helper;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            UserState state = user.getUserState();
            switch (state) {
                case SELECT_COURSE:
                    return helper.selectGroup(user, message);
                case SELECT_GROUP:
                    return selectGroupOrAccept(user, message);
                case SELECT_ROLE:
                    return switchRole(user, message);
                case INPUT_TEXT:
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
            User save = userRepository.save(user);
            return TelegramUtil.createSelectCourseButtonPanel(save);
        } else if (message.equalsIgnoreCase(ROLE_TEACHER)) {
            user.setRole(Role.TEACHER);
            return inputTeacherName(user);
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> inputTeacherName(User user) {
        user.setUserState(UserState.INPUT_TEXT);
        return List.of(HandlerHelper.inputMessage(
                userRepository.save(user),
                "Введите фамилию..."));
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectGroupOrAccept(User user, String message) throws ResourceNotFoundException {

        if (Course.isCourse(message)) {
            return helper.selectGroup(user, message);
        } else if (HandlerHelper.isNumeric(message)) {
            int id = Integer.parseInt(message);
            log.info("Find group by id: {} ...", id);
            Group group = groupRepository.findById(id)
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
        } else if (HandlerHelper.isNumeric(message)) {
            int id = Integer.parseInt(message);
            Teacher teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(id, Teacher.class));
            user.setTeacher(teacher);
            return List.of(
                    accept(user)
            );
        }
        return Collections.emptyList();
    }

    private List<PartialBotApiMethod<? extends Serializable>> selectTeacher(User user, String message) {
        List<IdTeacherName> teacherNames = teacherRepository.findAllByName(message.toLowerCase());
        if (!teacherNames.isEmpty()) {
            user.setUserState(UserState.SELECT_TEACHER);
            User save = userRepository.save(user);

            final InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
            markup1.setKeyboard(TelegramUtil.createInlineKeyboardButtons(teacherNames, 2));

            final ReplyKeyboardMarkup markup2 = TelegramUtil.createReplyKeyboardMarkup();
            final KeyboardRow row = TelegramUtil.createKeyboardRow(List.of(ConstantsHandler.CANCEL));
            markup2.setKeyboard(List.of(row))
                    .setOneTimeKeyboard(true);

            return sendMessageItIsYou(save, markup1, markup2);
        }
        return sendMessageNotFound(user);
    }


    private List<PartialBotApiMethod<? extends Serializable>> sendMessageItIsYou(User user,
                                                                                 InlineKeyboardMarkup markup1,
                                                                                 ReplyKeyboardMarkup markup2) {
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Выберите педагога")
                        .setReplyMarkup(markup1),
                TelegramUtil.createMessageTemplate(user)
                        .setText("...?")
                        .setReplyMarkup(markup2));
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMessageNotFound(User user) {
        String outMessage = "Педагог не найден";
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
        User save = userRepository.save(user);
        return HandlerHelper.mainMessage(save);
    }

    private PartialBotApiMethod<? extends Serializable> cancel(User user) {
        user.setUserState(UserState.SELECT_ROLE);
        user.setBotState(BotState.REG);
        final User saveUser = userRepository.save(user);
        return HandlerHelper.selectRole(saveUser);
    }

    @Override
    public BotState operatedBotState() {
        return BotState.REG;
    }

    @Override
    public List<Role> operatedUserRoles() {
        return List.of(
                Role.STUDENT,
                Role.NONE,
                Role.TEACHER
        );
    }

    @Override
    public List<UserState> operatedUserSate() {
        return List.of(
                UserState.SELECT_ROLE,
                UserState.SELECT_COURSE,
                UserState.SELECT_TEACHER,
                UserState.SELECT_GROUP,
                UserState.INPUT_TEXT
        );
    }
}
