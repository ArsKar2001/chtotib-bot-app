package com.karmanchik.chtotibtelegrambot.jpa.service.impl;

import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Replacement;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;
import com.karmanchik.chtotibtelegrambot.jpa.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final JpaGroupRepository groupRepository;
    private final EntityManager entityManager;

    @Override
    public Group getByName(String name) {
        return groupRepository.getByName(name)
                .orElseGet(() -> groupRepository
                        .save(Group.builder(name)
                                .build()));
    }

    @Override
    public List<IdGroupName> getAllGroupName() {
        return groupRepository.getAllGroupName();
    }

    @Override
    public <S extends Group> S save(S s) {
        return groupRepository.save(s);
    }

    @Override
    public void deleteById(Integer id) {
        groupRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        log.info("Deleted all groups!");
        groupRepository.deleteAllInBatch();
    }

    @Override
    public List<Lesson> getLessonsByGroupId(Integer id) {
        return entityManager.createQuery(
                "SELECT l FROM Lesson l " +
                        "WHERE l.group.id = :id " +
                        "ORDER BY l.day, l.pairNumber")
                .setParameter("id", id).getResultList();
    }

    @Override
    public List<Replacement> getReplacementByGroupId(Integer id) {
        return entityManager.createQuery(
                "SELECT r FROM Replacement r " +
                        "WHERE r.group.id = :id " +
                        "ORDER BY r.date, r.pairNumber")
                .setParameter("id", id).getResultList();
    }

    @Override
    public List<IdGroupName> getAllGroupNameByYearSuffix(String academicYearSuffix) {
        return groupRepository.getAllGroupNameByYearSuffix(academicYearSuffix);
    }

    @Override
    public List<Group> saveAll(List<Group> t) {
        return groupRepository.saveAll(t);
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public Optional<Group> findById(Integer id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }
}
