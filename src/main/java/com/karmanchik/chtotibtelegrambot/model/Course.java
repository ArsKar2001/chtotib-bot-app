package com.karmanchik.chtotibtelegrambot.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Course {

    I(1),
    II(2),
    III(3),
    IV(4);

    private final int value;

    Course(int value) {
        this.value = value;
    }

    public static boolean isCourse(Object o) {
        for (Course course : values()) {
            if (course.equals(o)) {
                return true;
            }
            if (course.name().equals(o)) {
                return true;
            }
            if (o.getClass().isMemberClass() && course.value == (Integer) o) {
                return true;
            }
        }
        return false;
    }

    public static List<String> names() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }
}
