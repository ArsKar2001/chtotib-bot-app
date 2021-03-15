package com.karmanchik.chtotibtelegrambot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Courses {
    private static final Map<String, String> COURSES = Map.of(
            "I", "1",
            "II", "2",
            "III", "3",
            "IV", "4"
    );

    public static Map<String, String> getAll() {
        return COURSES;
    }

    public static boolean containsKey(Object o) {
        return COURSES.containsKey(o);
    }

    public static boolean containsValue(Object o) {
        return COURSES.containsValue(o);
    }

    public static String get(Object o) {
        return COURSES.get(o);
    }

    public static List<String> getKeys() {
        return new ArrayList<>(COURSES.keySet());
    }

    public static List<String> getValues() {
        return new ArrayList<>(COURSES.values());
    }
}