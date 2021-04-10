package com.karmanchik.chtotibtelegrambot.entity.enums;

public enum WeekType {
    NONE(0),
    UP(1),
    DOWN(2);

    private final int code;

    WeekType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
