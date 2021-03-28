package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService extends BaseService<Teacher> {

    <S extends Teacher> S getByName(String teacherName);

    Optional<List<String>> getAllNames();
}
