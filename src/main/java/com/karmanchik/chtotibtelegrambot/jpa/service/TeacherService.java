package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Replacement;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdTeacherName;

import java.util.List;

public interface TeacherService extends BaseService<Teacher> {

    Teacher getByName(String teacherName);

    List<Lesson> getLessonsByGroupId(Integer id);

    List<Replacement> getReplacementsByGroupId(Integer id);

    List<IdTeacherName> getAllIdTeacherName();

    List<IdTeacherName> getAllIdTeacherNameByName(String message);
}
