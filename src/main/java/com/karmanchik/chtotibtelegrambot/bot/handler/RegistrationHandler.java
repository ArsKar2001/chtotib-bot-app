package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.model.Role;
import com.karmanchik.chtotibtelegrambot.model.State;
import com.karmanchik.chtotibtelegrambot.repository.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.repository.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.Serializable;
import java.time.Month;
import java.util.*;

import static java.lang.Integer.parseInt;

@Component
public class RegistrationHandler implements Handler {

    public static final String ACCEPT = "сохранить";
    public static final String CHANGE = "изменить";
    public static final String CANCEL = "назад";

    private static final String SELECT_ROLE_STUDENT = Role.STUDENT.getValue().toUpperCase();
    private static final String SELECT_ROLE_TEACHER = Role.TEACHER.getValue().toUpperCase();

    private static final Map<String, String> COURSES = Map.of(
            "I", "1",
            "II", "2",
            "III", "3",
            "IV", "4"
    );

    private final JpaUserRepository userRepository;
    private final JpaGroupRepository groupRepository;

    public RegistrationHandler(JpaUserRepository userRepository, JpaGroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            switch (user.getUserState()) {
                case SELECT_ROLE:
                    return switchRole(user, message);
                case SELECT_COURSE:
                    return selectGroup(user, message);
                case SELECT_GROUP:
                    if (COURSES.containsKey(message)) {
                        return selectGroup(user, message);
                    }
                    if (isGroupId(message)) {
                        int groupId = parseInt(message);
                        user.setGroupId(groupId);
                        return acceptOrCancel(user);
                    }
                    return Collections.emptyList();
                case SELECT_OPTION:
                    if (message.equalsIgnoreCase(ACCEPT))
                        return accept(user);
                    if (message.equalsIgnoreCase(CHANGE))
                        return cancel(user);
                case ENTER_NAME:
                    return createSelectTeacherButtonsPanel(user, message);
                case SELECT_TEACHER:
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
        List<String> allTeachers = groupRepository.findAllTeachers();
        if (message.equalsIgnoreCase(CANCEL)) {
            user.setUserState(State.ENTER_NAME);
            userRepository.save(user);
            return inputTeacherName(user);
        } else if (allTeachers.contains(message)) {
            user.setName(message);
            userRepository.save(user);
            return acceptOrCancel(user);
        } else {
            return didNotDefine(user);
        }
    }

    static List<PartialBotApiMethod<? extends Serializable>> selectRole(User user) {
        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(
                SELECT_ROLE_STUDENT,
                SELECT_ROLE_TEACHER
        ));
        markup.setKeyboard(List.of(keyboardRow));
        markup.setOneTimeKeyboard(true);

        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Выбери роль...")
                        .setReplyMarkup(markup));
    }

    List<PartialBotApiMethod<? extends Serializable>> switchRole(User user, String message) {
        if (message.equalsIgnoreCase(SELECT_ROLE_STUDENT)) {
            user.setUserState(State.SELECT_COURSE);
            user.setRoleName(Role.STUDENT.name());
            userRepository.save(user);
            return createSelectCourseButtonsPanel(user);
        } else if (message.equalsIgnoreCase(SELECT_ROLE_TEACHER)) {
            user.setRoleName(Role.TEACHER.name());
            userRepository.save(user);
            return inputTeacherName(user);
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> selectGroup(User user, String message) {

        if (!COURSES.containsKey(message)) return Collections.emptyList();

        int academicYear = this.getAcademicYear(COURSES.get(message));
        String academicYearPostfix = String.valueOf(academicYear).substring(2);
        List<Group> groupList = groupRepository.getListGroupNameByYearSuffix(academicYearPostfix);
        Arrays.sort(groupList.toArray());

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(TelegramUtil.createGroupListInlineKeyboardButton(groupList, 3));

        user.setUserState(State.SELECT_GROUP);
        userRepository.save(user);
        return List.of(
                TelegramUtil.createMessageTemplate(user)
                        .setText("Выбери группу...")
                        .setReplyMarkup(keyboardMarkup)
        );
    }

    List<PartialBotApiMethod<? extends Serializable>> inputTeacherName(User user) {
        user.setUserState(State.ENTER_NAME);
        userRepository.save(user);
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
        user.setUserState(State.SELECT_OPTION);
        userRepository.save(user);

        ReplyKeyboardMarkup markup = TelegramUtil.createReplyKeyboardMarkup();
        KeyboardRow keyboardRow = TelegramUtil.createKeyboardRow(List.of(ACCEPT.toUpperCase(), CHANGE.toUpperCase()));
        markup.setKeyboard(List.of(keyboardRow));

        if (user.getRoleName().equalsIgnoreCase(Role.STUDENT.name())) {
            Group group = groupRepository
                    .findById(user.getGroupId())
                    .orElseThrow(() -> new RuntimeException("не найден group по id " + user.getGroupId()));
            return List.of(TelegramUtil.createMessageTemplate(user)
                    .setText(String.format(
                            "<b>Роль</b>: %s%n<b>Группа</b>: %s", Role.valueOf(user.getRoleName()).getValue(), group.getGroupName()))
                    .setReplyMarkup(markup));
        } else if (user.getRoleName().equalsIgnoreCase(Role.TEACHER.name())) {
            return List.of(TelegramUtil.createMessageTemplate(user)
                    .setText(String.format(
                            "<b>Роль</b>: %s%n<b>Имя</b>: %s", Role.valueOf(user.getRoleName()).getValue(), user.getName()))
                    .setReplyMarkup(markup));
        }
        return Collections.emptyList();
    }

    List<PartialBotApiMethod<? extends Serializable>> cancel(User user) {
        user.setUserState(State.SELECT_ROLE);
        user.setBotState(State.REG);
        userRepository.save(user);
        return selectRole(user);
    }

    List<PartialBotApiMethod<? extends Serializable>> accept(User user) {
        user.setUserState(State.NONE);
        user.setBotState(State.AUTHORIZED);
        userRepository.save(user);
        return List.of(TelegramUtil.mainMessage(user));
    }

    List<PartialBotApiMethod<? extends Serializable>> createSelectTeacherButtonsPanel(User user, String message) {
        user.setUserState(State.SELECT_TEACHER);
        userRepository.save(user);
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
        List<String> teachers = groupRepository.getListTeachersByName(message.toLowerCase());
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
        List<Integer> groups = groupRepository.getListGroupId();
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
    public State operatedBotState() {
        return State.REG;
    }

    @Override
    public List<State> operatedUserListState() {
        return List.of(
                State.SELECT_COURSE, State.SELECT_ROLE, State.SELECT_GROUP, State.SELECT_OPTION, State.ENTER_NAME, State.SELECT_TEACHER
        );
    }
}
