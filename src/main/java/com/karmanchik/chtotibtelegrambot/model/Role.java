package com.karmanchik.chtotibtelegrambot.model;

public enum Role {
    NONE(""),
    STUDENT("студент"),
    TEACHER("педагог");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
