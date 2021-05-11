package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.model.Course;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

class DateHelperTest {
    @Test
    void testDate() {
        System.out.println(DateHelper.getAcademicYearByCourse(Course.IV).format(DateTimeFormatter.ofPattern("yy")));
    }
}