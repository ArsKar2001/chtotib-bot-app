package com.karmanchik.chtotibtelegrambot.model;

public enum InstanceRole {
    NONE(100),
    TEACHER(101),
    STUDENT(102);

    private final int id;

    InstanceRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}