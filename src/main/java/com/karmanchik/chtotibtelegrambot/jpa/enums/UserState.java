package com.karmanchik.chtotibtelegrambot.jpa.enums;

public enum UserState {
    NONE(200),
    SELECT_COURSE(201),
    SELECT_GROUP(202),
    SELECT_OPTION(204),
    SELECT_ROLE(203),
    SELECT_TEACHER(206),
    SELECT_TIMETABLE(207),
    INPUT_TEXT(205),
    START(208);

    private final int code;

    UserState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
