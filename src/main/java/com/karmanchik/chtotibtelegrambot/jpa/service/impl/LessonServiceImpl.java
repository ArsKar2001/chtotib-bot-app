package com.karmanchik.chtotibtelegrambot.jpa.service.impl;

import com.karmanchik.chtotibtelegrambot.jpa.JpaLessonsRepository;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final JpaLessonsRepository lessonsRepository;

    @Override
    public <S extends Lesson> S save(S s) {
        return lessonsRepository.save(s);
    }

    @Override
    public void deleteById(Integer id) {
        lessonsRepository.deleteById(id);
    }

    @Override
    public <S extends Lesson> void delete(S s) {
        lessonsRepository.delete(s);
    }

    @Override
    public <S extends Lesson> void deleteAll() {
        log.info("Deleted all lessons!");
        lessonsRepository.deleteAllInBatch();
    }

    @Override
    public List<Lesson> saveAll(List<Lesson> t) {
        return lessonsRepository.saveAll(t);
    }

    @Override
    public Optional<Lesson> findById(Integer id) {
        return lessonsRepository.findById(id);
    }

    @Override
    public List<Lesson> findAll() {
        return lessonsRepository.findAll();
    }
}
