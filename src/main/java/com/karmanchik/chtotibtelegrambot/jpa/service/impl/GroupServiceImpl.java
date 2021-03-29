package com.karmanchik.chtotibtelegrambot.jpa.service.impl;

import com.karmanchik.chtotibtelegrambot.jpa.JpaGroupRepository;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;
import com.karmanchik.chtotibtelegrambot.jpa.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final JpaGroupRepository groupRepository;

    @Override
    public Group getByName(String name) {
        return groupRepository.getByName(name)
                .orElseGet(() -> groupRepository
                        .save(Group.builder(name)
                                .build()));
    }

    @Override
    public Optional<List<String>> getAllGroupName() {
        return groupRepository.getAllGroupName();
    }

    @Override
    public List<IdGroupName> getAllGroupNameByYearSuffix(String suffix) {
        return groupRepository.getAllGroupNameByYearSuffix(suffix);
    }

    @Override
    public boolean existsById(Integer id) {
        return groupRepository.existsById(id);
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
    public <S extends Group> void delete(S s) {
        groupRepository.delete(s);
    }

    @Override
    public <S extends Group> void deleteAll() {
        log.info("Deleted all groups!");
        groupRepository.deleteAllInBatch();
    }

    @Override
    public List<Group> saveAll(List<Group> t) {
        return groupRepository.saveAll(t);
    }

    @Override
    public Optional<Group> findById(Integer id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> findAll() {
        return null;
    }
}
