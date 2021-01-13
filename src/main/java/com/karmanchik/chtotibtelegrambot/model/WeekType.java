package com.karmanchik.chtotibtelegrambot.model;

public enum WeekType {
    NONE(""),
    UP("верхняя"),
    DOWN("нижняя");

    private final String value;

    WeekType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
