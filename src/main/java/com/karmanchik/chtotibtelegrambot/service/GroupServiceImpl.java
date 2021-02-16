package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.model.Lesson;
import com.karmanchik.chtotibtelegrambot.repository.JpaGroupRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

@Component
@Log4j
public class GroupServiceImpl implements GroupService {
    private final JpaGroupRepository groupRepository;

    public GroupServiceImpl(JpaGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public List<Integer> findAllGroupId() {
        return groupRepository.getListGroupId();
    }

    @Override
    public List<Group> findAllGroupNamesByYearSuffix(@NotNull String yearSuffix) {
        return groupRepository.getListGroupNameByYearSuffix(yearSuffix);
    }

    @Override
    public List<String> findAllTeachersByName(@NotNull String teacher) {
        return groupRepository.getListTeachersByName(teacher);
    }

    @Override
    public List<String> findAllTeachers() {
        return groupRepository.findAllTeachers();
    }

    @Override
    public List<Lesson> findAllLessonsByGroup(@NotNull String groupName) {
        return groupRepository.getListLessonByGroupName(groupName);
    }

    @Override
    public List<Lesson> findAllLessonsByGroup(@NotNull String groupName, @NotNull String weekType) {
        return groupRepository.findAllLessonsByGroupNameAndWeekType(groupName, weekType);
    }

    @Override
    public List<Lesson> findAllLessonsByTeacher(@NotNull String teacher, @NotNull String weekType) {
        return groupRepository.findAllLessonsByTeacherAndWeekType(teacher, weekType);
    }

    @Override
    public List<Integer> findAllDaysOfWeekByGroupName(@NotNull String groupName) {
        return groupRepository.getListDaysOfWeekByGroupName(groupName);
    }

    @Override
    public List<Integer> findAllDaysOfWeekByTeacher(@NotNull String teacher) {
        return groupRepository.getListDaysOfWeekByTeacher(teacher);
    }

    @Override
    public List<Lesson> findAllByGroupNameAndDayOfWeek(@NotNull String groupName, @NotNull Integer dayOfWeek, @NotNull String weekType) {
        return groupRepository.findAllByGroupNameAndDayOfWeek(groupName, dayOfWeek, weekType);
    }

    @Override
    public List<Lesson> findAllByTeacherAndDayOfWeek(@NotNull String teacher, @NotNull Integer dayOfWeek, @NotNull String weekType) {
        return groupRepository.findAllByTeacherAndDayOfWeek(teacher, dayOfWeek, weekType);
    }

    @Override
    public Group findById(Integer groupId) {
        return groupRepository.findById(groupId).orElseGet(Group::new);
    }
}
