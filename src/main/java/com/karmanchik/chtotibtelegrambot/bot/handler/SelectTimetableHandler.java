package com.karmanchik.chtotibtelegrambot.bot.handler;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Log4j2
@Component
@RequiredArgsConstructor
public class SelectTimetableHandler implements Handler {


    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (Command.isCommand(message)) {
            Command command = Command.valueByKey(message);
            switch (command) {
                case TIMETABLE_TOMORROW:
                    log.debug("!!!! log debug 1: select handler - getTimetableNextDay for {}", user.toString());
                    return getTimetableNextDay(user);
                case TIMETABLE_FULL:
                    break;
                case TIMETABLE_TEACHER:
                    break;
                case TIMETABLE_GROUP:
                    break;
                case CANCEL:
                    break;
            }
        }
        return null;
    }

    private List<PartialBotApiMethod<? extends Serializable>> getTimetableNextDay(User user) {
        return null;
    }

    @Override
    public Integer operatedBotStateId() {
        return Constants.Bot.AUTHORIZED;
    }

    @Override
    public List<Integer> operatedUserListStateId() {
        return List.of(Constants.User.SELECT_TIMETABLE);
    }

    public enum Command {
        TIMETABLE_TOMORROW,
        TIMETABLE_FULL,
        TIMETABLE_TEACHER,
        TIMETABLE_GROUP,
        CANCEL;

        public static final Map<String, Command> COMMAND_MAP = new HashMap<>();

        private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\d+");

        static {
            for (var value : values()) {
                int key = value.ordinal() + 1;
                COMMAND_MAP.put(String.valueOf(key), value);
            }
        }

        public static Command valueByKey(String s) {
            return COMMAND_MAP.get(s);
        }

        public static boolean isCommand(String s) {
            return COMMAND_PATTERN.matcher(s).matches();
        }
    }
}
