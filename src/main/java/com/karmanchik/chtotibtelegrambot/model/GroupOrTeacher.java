package com.karmanchik.chtotibtelegrambot.model;

import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.Replacement;

import java.util.List;
import java.util.Set;

public interface GroupOrTeacher {
    Integer getId();
    String getName();
    Set<Lesson> getLessons();
    Set<Replacement> getReplacements();
}
