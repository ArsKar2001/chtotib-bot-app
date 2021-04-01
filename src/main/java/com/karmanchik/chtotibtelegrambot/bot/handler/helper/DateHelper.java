package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.jpa.enums.WeekType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateHelper {
    private DateHelper() {
    }

    public static LocalDate getNextSchoolDate() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() > 12) {
            if (now.getDayOfWeek().getValue() < 5)
                now = now.plusDays(1);
            else do {
                now = now.plusDays(1);
            } while (now.getDayOfWeek() != DayOfWeek.MONDAY);
        }
        return now.toLocalDate();
    }

    public static WeekType getWeekType() {
        LocalDate now = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfYear();
        int weekNumber = now.get(weekOfYear);
        return weekNumber % 2 == 0 ? WeekType.UP : WeekType.DOWN;
    }
}