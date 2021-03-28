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
    public <S extends Group> S getByName(String name) {
        return (S) groupRepository.getByName(name)
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
    public <S extends Group> List<S> saveAll(List<S> s) {
        return groupRepository.saveAll(s);
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
    public <S extends Group> Optional<S> findById(Integer id) {
        return (Optional<S>) groupRepository.findById(id);
    }

    @Override
    public <S extends Group> List<S> findAll() {
        return (List<S>) groupRepository.findAll();
    }
}
