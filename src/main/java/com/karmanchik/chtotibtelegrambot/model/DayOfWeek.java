package com.karmanchik.chtotibtelegrambot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DayOfWeek {
    private static final Map<String, Integer> DAYS_OF_WEEK = Map.of(
            "Понедельник", 0,
            "Вторник", 1,
            "Среда", 2,
            "Четверг", 3,
            "Пятница", 4,
            "Суббота", 5,
            "Воскресенье", 6
    );

    private DayOfWeek() {
    }

    public static boolean containsKey(String s) {
        return DAYS_OF_WEEK.containsKey(s);
    }

    public static boolean containsValue(Integer value) {
        return DAYS_OF_WEEK.containsValue(value);
    }

    public static Integer get(String s) {
        return DAYS_OF_WEEK.get(s);
    }

    public static List<String> getKeys() {
        return new ArrayList<>(DAYS_OF_WEEK.keySet());
    }

    public static List<Integer> getValues() {
        return new ArrayList<>(DAYS_OF_WEEK.values());
    }
}
