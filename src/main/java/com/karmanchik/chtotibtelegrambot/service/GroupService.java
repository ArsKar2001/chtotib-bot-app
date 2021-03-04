package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.model.Lesson;
import com.karmanchik.chtotibtelegrambot.repository.JpaGroupRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Log4j2
public class GroupService {
    private final JpaGroupRepository groupRepository;

    public GroupService(JpaGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Integer> findAllGroupId() {
        return groupRepository.findAllGroupId();
    }

    public List<Group> findAllGroupNamesByYearSuffix(@NotNull String yearSuffix) {
        return groupRepository.findAllGroupByYearSuffix(yearSuffix);
    }

    public List<String> findAllTeachersByName(@NotNull String teacher) {
        return groupRepository.findAllTeachersByName(teacher);
    }

    public List<String> findAllTeachers() {
        return groupRepository.findAllTeachers();
    }

    public List<Lesson> findAllLessonsByGroup(@NotNull String groupName) {
        return groupRepository.findAllLessonsByGroupName(groupName);
    }

    public List<Lesson> findAllLessonsByGroup(@NotNull String groupName, @NotNull String weekType) {
        return groupRepository.findAllLessonsByGroupNameAndWeekType(groupName, weekType);
    }

    public List<Lesson> findAllLessonsByTeacher(@NotNull String teacher, @NotNull String weekType) {
        return groupRepository.findAllLessonsByTeacherAndWeekType(teacher, weekType);
    }

    public List<Integer> findAllDaysOfWeekByGroupName(@NotNull String groupName) {
        return groupRepository.getListDaysOfWeekByGroupName(groupName);
    }

    public List<Integer> findAllDaysOfWeekByTeacher(@NotNull String teacher) {
        return groupRepository.getListDaysOfWeekByTeacher(teacher);
    }

    public List<Lesson> findAllByGroupNameAndDayOfWeek(@NotNull String groupName, @NotNull Integer dayOfWeek, @NotNull String weekType) {
        return groupRepository.findAllByGroupNameAndDayOfWeek(groupName, dayOfWeek, weekType);
    }

    public List<Lesson> findAllByTeacherAndDayOfWeek(@NotNull String teacher, @NotNull Integer dayOfWeek, @NotNull String weekType) {
        return groupRepository.findAllByTeacherAndDayOfWeek(teacher, dayOfWeek, weekType);
    }

    public Group findById(Integer groupId) {
        return groupRepository.findById(groupId).orElseGet(Group::new);
    }
}
