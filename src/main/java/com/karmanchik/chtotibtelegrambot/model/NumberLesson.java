package com.karmanchik.chtotibtelegrambot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NumberLesson {
    private static final Map<String, Integer> COURSES = Map.of(
            "I", 1,
            "II", 2,
            "III", 3,
            "IV", 4,
            "V", 5,
            "VI", 6,
            "VII", 7,
            "VIII", 8

    );

    private NumberLesson() {
    }

    public static Map<String, Integer> getAll() {
        return COURSES;
    }

    public static boolean containsKey(String s) {
        return COURSES.containsKey(s);
    }

    public static boolean containsValue(Integer integer) {
        return COURSES.containsValue(integer);
    }

    public static Integer get(String s) {
        return COURSES.get(s);
    }

    public static List<String> getKeys() {
        return new ArrayList<>(COURSES.keySet());
    }

    public static List<Integer> getValues() {
        return new ArrayList<>(COURSES.values());
    }
}