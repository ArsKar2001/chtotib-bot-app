package com.karmanchik.chtotibtelegrambot.jpa.enums;

public enum BotState {
    START(100),
    REG(101),
    AUTHORIZED(102);

    private final int code;

    BotState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
