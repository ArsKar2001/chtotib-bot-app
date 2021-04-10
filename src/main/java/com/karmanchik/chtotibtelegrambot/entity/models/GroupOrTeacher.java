package com.karmanchik.chtotibtelegrambot.entity.models;

import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.Replacement;

import java.util.List;

public interface GroupOrTeacher {
    String getName();
    List<Lesson> getLessons();
    List<Replacement> getReplacements();
}
