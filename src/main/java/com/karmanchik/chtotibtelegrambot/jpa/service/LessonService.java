package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;

import java.util.List;

public interface LessonService extends BaseService<Lesson> {
    @Override
    <S extends Lesson> S save(S s);

    @Override
    <S extends Lesson> List<S> saveAll(List<S> s);
}
