package com.karmanchik.chtotibtelegrambot.bot.handler.helper;

import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.model.NumberLesson;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Helper {
    public static Locale getLocale() {
        return Locale.forLanguageTag("ru");
    }

    public static Consumer<Lesson> getLessonTeacher(StringBuilder message) {
        return lesson -> {
            String number = NumberLesson.get(lesson.getPairNumber());
            message.append("\t")
                    .append(number).append("\t<b>|</b>\t")
                    .append(lesson.getDiscipline()).append("\t<b>|</b>\t")
                    .append(lesson.getAuditorium()).append("\t<b>|</b>\t")
                    .append(lesson.getGroup().getName())
                    .append("\n");
        };
    }

    public static Consumer<Lesson> getLessonGroup(StringBuilder message) {
        return lesson -> {
            String number = NumberLesson.get(lesson.getPairNumber());
            message.append("\t")
                    .append(number).append("\t<b>|</b>\t")
                    .append(lesson.getDiscipline()).append("\t<b>|</b>\t")
                    .append(lesson.getAuditorium()).append("\t<b>|</b>\t")
                    .append(lesson.getTeachers().stream()
                            .map(Teacher::getName)
                            .collect(Collectors.joining(", ")))
                    .append("\n");
        };
    }
}
