package com.karmanchik.chtotibtelegrambot.model;


import java.util.HashMap;
import java.util.Map;

public class Month {
    private static final Map<String, Integer> MONTH = new HashMap<>();

    static {
        String[] months = {"января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        for (int i = 0; i < months.length; i++) MONTH.put(months[i], i + 1);
    }

    private Month() {
    }

    public static boolean isMonth(String s) {
        return MONTH.containsKey(s);
    }

    public static Integer getNumberMonth(String s) {
        return MONTH.get(s);
    }
}
