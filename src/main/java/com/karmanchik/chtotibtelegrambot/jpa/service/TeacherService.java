package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdTeacherName;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface TeacherService extends BaseService<Teacher> {

    Teacher getByName(String teacherName);

    List<IdTeacherName> getIdTeacherName();

    List<IdTeacherName> getAllIdTeacherNameByNameLike(@NotNull String suffix);
}
