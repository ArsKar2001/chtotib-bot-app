package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdTeacherName;

import java.util.List;

public interface TeacherService extends BaseService<Teacher> {

    Teacher getByName(String teacherName);

    List<IdTeacherName> getAllIdTeacherName();

    List<IdTeacherName> getAllIdTeacherNameByName(String message);
}
