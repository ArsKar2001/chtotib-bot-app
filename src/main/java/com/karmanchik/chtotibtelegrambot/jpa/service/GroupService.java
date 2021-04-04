package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Replacement;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;

import java.util.List;

public interface GroupService extends BaseService<Group> {
    Group getByName(String name);

    List<IdGroupName> getAllGroupName();

    List<IdGroupName> getAllGroupNameByYearSuffix(String academicYearSuffix);
}
