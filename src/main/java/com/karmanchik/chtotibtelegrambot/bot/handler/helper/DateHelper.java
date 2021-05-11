package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import com.karmanchik.chtotibtelegrambot.model.Course;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateHelper {
    private DateHelper() {
    }

    public static LocalDate getNextSchoolDate() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfWeek().getValue() >= 5) {
            do now = now.plusDays(1);
            while (now.getDayOfWeek() != DayOfWeek.MONDAY);
        } else if (now.getHour() > 12) {
            now = now.plusDays(1);
        }
        return now.toLocalDate();
    }

    public static Year getAcademicYearByCourse(Course course) {
        LocalDate now = LocalDate.now();
        Month nowMonth = now.getMonth();
        int value = course.getValue();
        int diffYear = Year.now().getValue() - value;
        return nowMonth.getValue() > Month.SEPTEMBER.getValue() ?
                Year.of(diffYear + 1) :
                Year.of(diffYear);
    }

    public static WeekType getWeekType() {
        LocalDate now = LocalDate.now();
        int weekNumber = now.get(WeekFields.of(Locale.getDefault()).weekOfYear());
        return weekNumber % 2 == 0 ? WeekType.UP : WeekType.DOWN;
    }
}
