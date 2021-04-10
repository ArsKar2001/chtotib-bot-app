package com.karmanchik.chtotibtelegrambot.entity.enums;

public enum Role {
    STUDENT(102), TEACHER(101), NONE(100);

    private final int code;

    Role(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
