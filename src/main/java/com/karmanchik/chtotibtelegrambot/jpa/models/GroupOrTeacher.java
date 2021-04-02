package com.karmanchik.chtotibtelegrambot.jpa.models;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Replacement;

import java.util.List;

public interface GroupOrTeacher {
    String getName();
    List<Lesson> getLessons();
    List<Replacement> getReplacements();
}
