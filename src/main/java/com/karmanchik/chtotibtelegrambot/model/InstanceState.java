package com.karmanchik.chtotibtelegrambot.model;

public enum InstanceState {
    NONE(200),
    SELECT_COURSE(201),
    SELECT_GROUP(202),
    SELECT_ROLE(203),
    SELECT_OPTION(204),
    ENTER_NAME(205),
    SELECT_TEACHER(206),
    START(100),
    REG(101),
    AUTHORIZED(102);

    private final int id;

    InstanceState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}