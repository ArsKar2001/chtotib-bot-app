package com.karmanchik.chtotibtelegrambot.bot.command;

import java.util.*;

public enum MainCommand {
    COMMAND_1,
    COMMAND_2,
    COMMAND_3,
    COMMAND_4;
    private static final Map<String, MainCommand> COMMAND_MAP = new HashMap<>();

    static {
        Arrays.stream(MainCommand.values()).forEach(c -> {
            String key = String.valueOf(c.ordinal() + 1);
            COMMAND_MAP.put(key, c);
        });
    }

    public static boolean isCommand(String message) {
        return MainCommand.getMap().containsKey(message);
    }

    public static List<String> getKeyAll() {
        return new ArrayList<>(COMMAND_MAP.keySet());
    }

    public static MainCommand get(String key) {
        return COMMAND_MAP.get(key);
    }

    public static Map<String, MainCommand> getMap() {
        return COMMAND_MAP;
    }
}
