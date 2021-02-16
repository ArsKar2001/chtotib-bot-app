package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.*;
import com.karmanchik.chtotibtelegrambot.service.GroupService;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Month;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.entity.BotState.Instance.REG;
import static com.karmanchik.chtotibtelegrambot.entity.Role.Instance.STUDENT;
import static com.karmanchik.chtotibtelegrambot.entity.Role.Instance.TEACHER;
import static com.karmanchik.chtotibtelegrambot.entity.UserState.Instance.*;
import static java.lang.Integer.parseInt;

@Log4j
@Component
public class RegistrationHandler implements Handler {

    public static final String ACCEPT = "сохранить";
    public static final String CHANGE = "изменить";
    public static final String CANCEL = "назад";

    private static final String ROLE_STUDENT = "студент";
    private static final String ROLE_TEACHER = "педагог";

    private static final Map<String, String> COURSES = Map.of(
            "I", "1",
            "II", "2",
            "III", "3",
            "IV", "4"
    );

    private final UserService userService;
    private final GroupService groupService;

    public RegistrationHandler(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            switch (user.getUserState().getCode()) {
                case "SELECT_ROLE":
                    return switchRole(user, message);
                case "SELECT_COURSE":
                    return selectGroup(user, message);
                case "SELECT_GROUP":
                    if (COURSES.containsKey(message)) {
                        return selectGroup(user, message);
                    }
                    if (isGroupId(message)) {
                        int groupId = parseInt(message);
                        user.setGroupId(groupId);
                        return acceptOrCancel(user);
                    }
                    return Collections.emptyList();
                case "SELECT_OPTION":
                    if (message.equalsIgnoreCase(ACCEPT))
                        return accept(user);
                    if (message.equalsIgnoreCase(CHANGE))
                        return cancel(user);
                case "ENTER_NAME":
                    return createSelectTeacherButtonsPanel(user, message);
                case "SELECT_TEACHER":
                    return selectTeacher(user, message);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(
                    TelegramUtil.createMessageTemplate(user)
                            .setText("<b>Ошибка</b>: " + e.getMessage())
            );
        }
    }

    List<PartialBotApiMethod<? extends Serializable>> selectTeacher(User user, String message) {
        List<String> allTeachers = groupService.findAllTeachers();
        if (message.equalsIgnoreCase(CANCEL)) {
            user.setUserStateId(UserState.Instance.ENTER_NAME.getId());
            userService.save(user);
            return inputTeacherName(user);
        } else if (allTeachers.contains(message)) {
            user.setName(message);
            userService.save(user);
            return acceptOrCancel(user);
        } else {
            return didNotDefine(user);
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
                        .setText("Выбери роль...")
                        .setReplyMarkup(markup));
    }

    List<PartialBotApiMethod<? extends Serializable>> switchRole(User user, String message) {
        if (message.equalsIgnoreCase(ROLE_STUDENT)) {
            user.setUserStateId(SELECT_COURSE.getId());
            user.setRoleId(STUDENT.getId());
            userService.save(user);
            return createSelectCourseButtonsPanel(user);
        } else if (message.equalsIgnoreCase(ROLE_TEACHER)) {
            user.setRoleId(TEACHER.getId());
            userService.save(user);
            return inputTeacherName(user);
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> selectGroup(User user, String message) {

        if (!COURSES.containsKey(message)) return Collections.emptyList();

        int academicYear = this.getAcademicYear(COURSES.get(message));
        String academicYearPostfix = String.valueOf(academicYear).substring(2);
        List<Group> groupList = groupService.getListGroupNameByYearSuffix(academicYearPostfix);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(TelegramUtil.createGroupListInlineKeyboardButton(groupList, 3));

        user.setUserStateId(SELECT_GROUP.getId());
        userService.save(user);
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Выбери группу...")
                        .setReplyMarkup(keyboardMarkup)
        );
    }

    List<PartialBotApiMethod<? extends Serializable>> inputTeacherName(User user) {
        user.setUserStateId(UserState.Instance.ENTER_NAME.getId());
        userService.save(user);
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Введите свою фамилию...")
                        .enableMarkdown(false)
        );
    }

    List<PartialBotApiMethod<? extends Serializable>> createSelectCourseButtonsPanel(User user) {
        List<String> values = new ArrayList<>(COURSES.keySet());
        Collections.sort(values);

        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(values);
        markup.setKeyboard(List.of(keyboardRow));

        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Выбери курс...")
                        .setReplyMarkup(markup)
        );
    }

    List<PartialBotApiMethod<? extends Serializable>> acceptOrCancel(User user) {
        user.setUserStateId(UserState.Instance.SELECT_OPTION.getId());
        userService.save(user);

        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(ACCEPT.toUpperCase(), CHANGE.toUpperCase()));
        markup.setKeyboard(List.of(keyboardRow));

        log.debug("!!!!! log debug: create KeyboardRow - " + markup.toString());

        @NotBlank Integer groupId = user.getGroupId();
        Group group = groupService.findById(groupId);
        log.debug("!!!!! log debug: получили group - " + group.toString());
        Role role = user.getRole();
        log.debug("!!!!! log debug: получили role - " + role.toString());

        if (role.getId() == STUDENT.getId()) {
            return List.of(TelegramUtil.createMessageTemplate(user)
                    .setText(String.format(
                            "<b>Роль</b>: %s%n<b>Группа</b>: %s", role.getDescription(), group.getGroupName()))
                    .setReplyMarkup(markup));
        } else if (role.getId() == TEACHER.getId()) {
            return List.of(TelegramUtil.createMessageTemplate(user)
                    .setText(String.format(
                            "<b>Роль</b>: %s%n<b>Имя</b>: %s", role.getDescription(), user.getName()))
                    .setReplyMarkup(markup));
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> cancel(User user) {
        user.setUserStateId(SELECT_ROLE.getId());
        user.setBotStateId(REG.getId());
        userService.save(user);
        return selectRole(user);
    }

    List<PartialBotApiMethod<? extends Serializable>> accept(User user) {
        user.setUserStateId(UserState.Instance.NONE.getId());
        user.setBotStateId(BotState.Instance.AUTHORIZED.getId());
        userService.save(user);
        return List.of(TelegramUtil.mainMessage(user));
    }

    List<PartialBotApiMethod<? extends Serializable>> createSelectTeacherButtonsPanel(User user, String message) {
        user.setUserStateId(UserState.Instance.SELECT_TEACHER.getId());
        userService.save(user);
        List<String> teacherList = getListFullTeachers(message.toLowerCase());

        InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
        markup1.setKeyboard(TelegramUtil.createTeacherListInlineKeyboardButton(teacherList, 2));

        ReplyKeyboardMarkup markup2 = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(CANCEL.toUpperCase()));
        markup2.setKeyboard(List.of(keyboardRow));

        if (!teacherList.isEmpty()) {
            return itIsYou(user, markup1, markup2);
        } else {
            return didNotDefine(user);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> didNotDefine(User user) {
        String outMessage = "Не смог вас определить :(";
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(outMessage)
                        .enableMarkdown(false),
                cancel(user).get(0)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> itIsYou(User user, InlineKeyboardMarkup markup1, ReplyKeyboardMarkup markup2) {
        String outMessage = "Это Вы...";
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText(outMessage)
                        .setReplyMarkup(markup1),
                TelegramUtil.createMessageTemplate(user)
                        .setText("...?")
                        .setReplyMarkup(markup2)
        );
    }

    private List<String> getListFullTeachers(String message) {
        List<String> teachers = groupService.getListTeachersByName(message.toLowerCase());
        List<String> _teachers = new ArrayList<>();

        for (String s : teachers) {
            if (s.contains("||")) {
                String[] split = s.split("\\|\\|", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else if (s.contains(",")) {
                String[] split = s.split(",", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else if (s.contains("║")) {
                String[] split = s.split("║", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else if (s.contains("‖")) {
                String[] split = s.split("‖", -5);
                _teachers.add(split[0].trim());
                _teachers.add(split[1].trim());
            } else {
                _teachers.add(s);
            }
        }

        Set<String> set = new HashSet<>(_teachers);
        _teachers.clear();
        _teachers.addAll(set);
        return _teachers;
    }

    boolean isGroupId(String message) {
        List<Integer> groups = groupService.getListGroupId();
        return groups.contains(parseInt(message));
    }

    int getAcademicYear(String message) {
        Calendar calendar = Calendar.getInstance();
        int number = parseInt(message);
        int now_year = calendar.get(Calendar.YEAR);
        int now_month = calendar.get(Calendar.MONTH);
        int academicYear = now_year - number;
        return (now_month > Month.SEPTEMBER.getValue()) ? academicYear + 1 : academicYear;
    }

    @Override
    public BotState.Instance operatedBotState() {
        return REG;
    }

    @Override
    public List<UserState.Instance> operatedUserListState() {
        return List.of(
                SELECT_COURSE,
                SELECT_ROLE,
                SELECT_GROUP,
                SELECT_OPTION,
                ENTER_NAME,
                SELECT_TEACHER
        );
    }
}
