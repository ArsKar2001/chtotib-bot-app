package com.karmanchik.chtotibtelegrambot.jpa.service.impl;

import com.karmanchik.chtotibtelegrambot.jpa.JpaTeacherRepository;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdTeacherName;
import com.karmanchik.chtotibtelegrambot.jpa.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final JpaTeacherRepository teacherRepository;
    private final EntityManager entityManager;

    @Override
    public Teacher getByName(String teacherName) {
        return teacherRepository.getByName(teacherName)
                .orElseGet(() -> teacherRepository
                        .save(Teacher.builder(teacherName)
                                .build()));
    }

    @Override
    public <S extends Teacher> S save(S s) {
        return teacherRepository.save(s);
    }

    @Override
    public List<Teacher> saveAll(List<Teacher> s) {
        return teacherRepository.saveAll(s);
    }

    @Override
    public void deleteById(Integer id) {
        teacherRepository.deleteById(id);
    }

    @Override
    public void delete(Teacher t) {
        teacherRepository.delete(t);
    }

    @Override
    public void deleteAll() {
        log.info("Deleted all users!");
        teacherRepository.deleteAllInBatch();
    }

    @Override
    public List<?> getLessonsByGroupId(Integer id) {
        return entityManager.createQuery(
                "SELECT l FROM Lesson l " +
                        "WHERE l.group.id = :id " +
                        "ORDER BY l.day, l.pairNumber")
                .setParameter("id", id).getResultList();
    }

    @Override
    public List<?> getReplacementsByGroupId(Integer id) {
        return entityManager.createQuery(
                "SELECT r FROM Replacement r " +
                        "WHERE r.group.id = :id " +
                        "ORDER BY r.date, r.pairNumber")
                .setParameter("id", id).getResultList();
    }

    @Override
    public Optional<Teacher> findById(Integer id) {
        return teacherRepository.findById(id);
    }

    @Override
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    public List<IdTeacherName> getAllIdTeacherName() {
        return teacherRepository.getAllNames();
    }

    @Override
    public List<IdTeacherName> getAllIdTeacherNameByName(String message) {
        return teacherRepository.getAllIdTeacherNameByName(message);
    }
}
