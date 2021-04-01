package com.karmanchik.chtotibtelegrambot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NumberLesson {
    private static final Map<Integer, String> COURSES = Map.of(
            1,"I",
            2,"II",
            3,"III",
            4,"IV",
            5,"V",
            6,"VI",
            7,"VII",
            8,"VIII"

    );

    private NumberLesson() {
    }

    public static Map<Integer, String> getAll() {
        return COURSES;
    }

    public static boolean containsKey(Integer s) {
        return COURSES.containsKey(s);
    }

    public static boolean containsValue(String integer) {
        return COURSES.containsValue(integer);
    }

    public static String get(Integer i) {
        return COURSES.get(i);
    }

    public static List<Integer> getKeys() {
        return new ArrayList<>(COURSES.keySet());
    }

    public static List<String> getValues() {
        return new ArrayList<>(COURSES.values());
    }
}