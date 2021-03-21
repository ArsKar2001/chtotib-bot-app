package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.karmanchik.chtotibtelegrambot.util.TelegramUtil.createMessageTemplate;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainHandler implements Handler {

    private final UserService userService;
    private final Helper helper;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        try {
            if (Command.isCommand(message)) {
                final Command command = Command.valueOfKey(message);
                switch (command) {
                    case COM_1:
                        log.debug("!!!! log debug 1: select handler - getTimetable for {}", user.toString());
                        return getTimetable(user);
                    case COM_2:
                        log.debug("!!!! log debug 1: select handler - getMessageInfo for {}", user.toString());
                        return getMessageInfo(user);
                    case COM_3:
                        log.debug("!!!! log debug 1: select handler - getEditProfile for {}", user.toString());
                        return getEditProfile(user);
                    default:
                        log.warn("Unsupported command {} for user {}", command, user.getId());
                        return Collections.emptyList();
                }
            }
            return List.of(Helper.mainMessage(user));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return List.of(
                    createMessageTemplate(user)
                            .setText("Ошибка: " + e.getMessage() + ";" + e.getLocalizedMessage()),
                    Helper.mainMessage(user)
            );
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetable(User user) {
        return null;
    }

    private List<PartialBotApiMethod<? extends Serializable>> getMessageInfo(User user) {
        StringBuilder stringBuilder = new StringBuilder();

        if (userService.isStudent(user))
            stringBuilder.append("<b>Группа</b>:\t").append(user.getGroup().getGroupName()).append("\n");
        else stringBuilder.append("<b>Имя</b>:\t").append(user.getName()).append("\n");
        stringBuilder.append("\n<b>От разработчика</b>:")
                .append("\nЭто не финальная версия моего бота. " +
                        "Сейчас он работает только с расписанием, однако в будущем будет учитывать замену и кидать новости с сайта <a href=\"https://www.chtotib.ru/\">ЧТОТиБ</a>. " +
                        "По всем ошибкам, вопросам или предложениям писать мне - @l_karmanchik_l.").append("\n");

        return List.of(
                createMessageTemplate(user)
                        .setText(stringBuilder.toString()),
                Helper.mainMessage(user)
        );
    }

    private List<PartialBotApiMethod<? extends Serializable>> getEditProfile(User user) {
        user.setRoleId(Constants.Role.NONE);
        user.setGroupId(Constants.Group.NONE);
        user.setTeacher("");
        user.setUserStateId(Constants.User.SELECT_ROLE);
        user.setBotStateId(Constants.Bot.REG);
        final User saveUser = userService.save(user);
        return RegistrationHandler.selectRole(saveUser);
    }



    @Override
    public Integer operatedBotStateId() {
        return Constants.Bot.AUTHORIZED;
    }

    @Override
    public List<Integer> operatedUserListStateId() {
        return List.of(Constants.User.NONE);
    }

    public enum Command {
        COM_1, // Узнать расписание
        COM_2, // Справочное сообщение
        COM_3; // Изменить профиль
        public static final Map<String, Command> COMMAND_MAP = new HashMap<>();

        private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\d+");

        static {
            for (var value : values()) {
                int key = value.ordinal() + 1;
                COMMAND_MAP.put(String.valueOf(key), value);
            }
        }

        public static Command valueOfKey(String s) {
            return COMMAND_MAP.get(s);
        }

        public static boolean isCommand(String s) {
            return COMMAND_PATTERN.matcher(s).matches();
        }
    }
}
