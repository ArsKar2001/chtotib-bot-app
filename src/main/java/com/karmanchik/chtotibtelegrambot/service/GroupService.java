package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.model.Lesson;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface GroupService {
    List<Integer> findAllGroupId();

    List<Group> findAllGroupNamesByYearSuffix(@Param("year_suffix") @NotNull String yearSuffix);

    List<String> findAllTeachersByName(@Param("teacher") @NotNull String teacher);

    List<String> findAllTeachers();

    List<Lesson> findAllLessonsByGroup(@Param("groupName") @NotNull String groupName);

    List<Lesson> findAllLessonsByGroup(@Param("groupName") @NotNull String groupName, @Param("weekType") @NotNull String weekType);

    List<Lesson> findAllLessonsByTeacher(@Param("teacher") @NotNull String teacher, @Param("weekType") @NotNull String weekType);

    List<Integer> findAllDaysOfWeekByGroupName(@Param("groupName") @NotNull String groupName);

    List<Integer> findAllDaysOfWeekByTeacher(@Param("teacher") @NotNull String teacher);

    List<Lesson> findAllByGroupNameAndDayOfWeek(@Param("groupName") @NotNull String groupName, @Param("dayOfWeek") @NotNull Integer dayOfWeek, @Param("weekType") @NotNull String weekType);

    List<Lesson> findAllByTeacherAndDayOfWeek(@Param("teacher") @NotNull String teacher, @Param("dayOfWeek") @NotNull Integer dayOfWeek, @Param("weekType") @NotNull String weekType);

    Group findById(Integer groupId);
}
