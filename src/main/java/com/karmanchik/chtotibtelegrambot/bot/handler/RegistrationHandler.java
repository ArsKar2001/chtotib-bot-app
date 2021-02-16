package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.*;
import com.karmanchik.chtotibtelegrambot.entity.BotState.Instance;
import com.karmanchik.chtotibtelegrambot.service.GroupService;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.Month;
import java.util.*;

import static com.karmanchik.chtotibtelegrambot.entity.BotState.Instance.AUTHORIZED;
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
                    return selectOrAccept(user, message);
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

    private List<PartialBotApiMethod<? extends Serializable>> selectOrAccept(User user, String message) {
        if (COURSES.containsKey(message)) {
            return selectGroup(user, message);
        } else if (isGroupId(message)) {
            final int groupId = parseInt(message);
            user.setGroupId(groupId);
            final User saveUser = userService.save(user);
            return accept(saveUser);
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> selectTeacher(User user, String message) {
        List<String> allTeachers = groupService.findAllTeachers();
        if (message.equalsIgnoreCase(CANCEL)) {
            user.setUserStateId(UserState.Instance.ENTER_NAME.getId());
            final User saveUser1 = userService.save(user);
            return inputTeacherName(saveUser1);
        } else if (allTeachers.contains(message)) {
            user.setName(message);
            final User saveUser2 = userService.save(user);
            return accept(saveUser2);
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
            final User saveUser1 = userService.save(user);
            return createSelectCourseButtonsPanel(saveUser1);
        } else if (message.equalsIgnoreCase(ROLE_TEACHER)) {
            user.setRoleId(TEACHER.getId());
            final User saveUser2 = userService.save(user);
            return inputTeacherName(saveUser2);
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> selectGroup(User user, String message) {

        if (!COURSES.containsKey(message)) return Collections.emptyList();

        final String s = COURSES.get(message);
        int academicYear = this.getAcademicYear(s);
        String academicYearSuffix = String.valueOf(academicYear).substring(2);
        List<Group> groupList = groupService.findAllGroupNamesByYearSuffix(academicYearSuffix);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(TelegramUtil.createGroupListInlineKeyboardButton(groupList, 3));

        user.setUserStateId(SELECT_GROUP.getId());
        final User saveUser = userService.save(user);
        return List.of(
                TelegramUtil.createMessageTemplate(saveUser)
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

    List<PartialBotApiMethod<? extends Serializable>> cancel(User user) {
        user.setUserStateId(SELECT_ROLE.getId());
        user.setBotStateId(REG.getId());
        final User saveUser = userService.save(user);
        return selectRole(saveUser);
    }

    List<PartialBotApiMethod<? extends Serializable>> accept(User user) {
        user.setUserStateId(NONE.getId());
        user.setBotStateId(AUTHORIZED.getId());
        final User saveUser = userService.save(user);
        return List.of(TelegramUtil.mainMessage(saveUser));
    }

    List<PartialBotApiMethod<? extends Serializable>> createSelectTeacherButtonsPanel(User user, String message) {
        user.setUserStateId(UserState.Instance.SELECT_TEACHER.getId());
        final User saveUser = userService.save(user);
        List<String> teacherList = getListFullTeachers(message.toLowerCase());

        InlineKeyboardMarkup markup1 = new InlineKeyboardMarkup();
        markup1.setKeyboard(TelegramUtil.createTeacherListInlineKeyboardButton(teacherList, 2));

        ReplyKeyboardMarkup markup2 = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(CANCEL.toUpperCase()));
        markup2.setKeyboard(List.of(keyboardRow));

        if (!teacherList.isEmpty()) {
            return createMessageItIsYou(saveUser, markup1, markup2);
        } else {
            return didNotDefine(saveUser);
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

    private List<PartialBotApiMethod<? extends Serializable>> createMessageItIsYou(User user, InlineKeyboardMarkup markup1, ReplyKeyboardMarkup markup2) {
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
        List<String> teachers = groupService.findAllTeachersByName(message.toLowerCase());
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
        List<Integer> groups = groupService.findAllGroupId();
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
    public Instance operatedBotState() {
        return REG;
    }

    @Override
    public List<UserState.Instance> operatedUserListState() {
        return List.of(
                SELECT_COURSE,
                SELECT_ROLE,
                SELECT_GROUP,
                ENTER_NAME,
                SELECT_TEACHER
        );
    }
}
