package com.karmanchik.chtotibtelegrambot.bot.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum MainCommand {
    COM_1,
    COM_2,
    COM_3,
    COM_4;
    public static final Map<String, MainCommand> COMMAND_MAP = new HashMap<>();

    private static final Pattern COMMAND_PATTERN = Pattern.compile("^\\d+");

    static {
        for (var value : values()) {
            int key = value.ordinal() + 1;
            COMMAND_MAP.put(String.valueOf(key), value);
        }
    }

    public static MainCommand valueOfKey(String s) {
        return COMMAND_MAP.get(s);
    }

    public static boolean isCommand(String s) {
        return COMMAND_PATTERN.matcher(s).matches();
    }
}
