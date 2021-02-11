package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.model.Lesson;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface GroupService {
    List<Integer> getListGroupId();

    List<Group> getListGroupNameByYearSuffix(@Param("year_suffix") @NotNull String yearSuffix);

    List<String> getListTeachersByName(@Param("teacher") @NotNull String teacher);

    List<String> findAllTeachers();

    List<Lesson> getListLesson(@Param("groupName") @NotNull String groupName);

    List<Lesson> getListLesson(@Param("groupName") @NotNull String groupName, @Param("weekType") @NotNull String weekType);

    List<Lesson> getListLessonByTeacher(@Param("teacher") @NotNull String teacher, @Param("weekType") @NotNull String weekType);

    List<Integer> getListDaysOfWeekByGroupName(@Param("groupName") @NotNull String groupName);

    List<Integer> getListDaysOfWeekByTeacher(@Param("teacher") @NotNull String teacher);

    List<Lesson> findAllByGroupNameAndDayOfWeek(@Param("groupName") @NotNull String groupName, @Param("dayOfWeek") @NotNull Integer dayOfWeek, @Param("weekType") @NotNull String weekType);

    List<Lesson> findAllByTeacherAndDayOfWeek(@Param("teacher") @NotNull String teacher, @Param("dayOfWeek") @NotNull Integer dayOfWeek, @Param("weekType") @NotNull String weekType);

    Group findById(Integer groupId);
}
