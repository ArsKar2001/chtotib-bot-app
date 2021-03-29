package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;

import java.util.List;
import java.util.Optional;

public interface GroupService extends BaseService<Group> {
    Group getByName(String name);

    Optional<List<String>> getAllGroupName();

    List<IdGroupName> getAllGroupNameByYearSuffix(String suffix);

    boolean existsById(Integer id);
}
