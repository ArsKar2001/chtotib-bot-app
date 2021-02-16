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
    public List<Integer> getListGroupId() {
        return groupRepository.getListGroupId();
    }

    @Override
    public List<Group> getListGroupNameByYearSuffix(@NotNull String yearSuffix) {
        return groupRepository.getListGroupNameByYearSuffix(yearSuffix);
    }

    @Override
    public List<String> getListTeachersByName(@NotNull String teacher) {
        return groupRepository.getListTeachersByName(teacher);
    }

    @Override
    public List<String> findAllTeachers() {
        return groupRepository.findAllTeachers();
    }

    @Override
    public List<Lesson> getListLesson(@NotNull String groupName) {
        return groupRepository.getListLessonByGroupName(groupName);
    }

    @Override
    public List<Lesson> getListLesson(@NotNull String groupName, @NotNull String weekType) {
        return groupRepository.getListLessonByGroupNameAndWeekType(groupName, weekType);
    }

    @Override
    public List<Lesson> getListLessonByTeacher(@NotNull String teacher, @NotNull String weekType) {
        return groupRepository.getListLessonByTeacherAndWeekType(teacher, weekType);
    }

    @Override
    public List<Integer> getListDaysOfWeekByGroupName(@NotNull String groupName) {
        return groupRepository.getListDaysOfWeekByGroupName(groupName);
    }

    @Override
    public List<Integer> getListDaysOfWeekByTeacher(@NotNull String teacher) {
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
