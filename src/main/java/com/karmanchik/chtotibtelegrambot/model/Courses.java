package com.karmanchik.chtotibtelegrambot.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static boolean containsKey(String s) {
        return COURSES.containsKey(s);
    }

    public static boolean containsValue(String s) {
        return COURSES.containsValue(s);
    }

    public static String get(String s) {
        return COURSES.get(s);
    }

    public static List<String> getKeys() {
        return new ArrayList<>(COURSES.keySet());
    }

    public static List<String> getValues() {
        return new ArrayList<>(COURSES.values());
    }
}