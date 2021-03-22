package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;

import java.util.List;

public interface GroupService {
    List<String> findAllTeachersByName(String teacher);

    List<Group> findAllGroupNamesByYearSuffix(String academicYearSuffix);

    boolean isGroupId(String message);

    List<String> findAllTeachers();

    List<Lesson> findAllByGroupNameAndDayOfWeek(String groupName, int nextDayOfWeek, String name);

    List<Lesson> findAllByTeacherAndDayOfWeek(String teacher, int nextDayOfWeek, String name);

    List<Integer> findAllDaysOfWeekByGroupName(String groupName);

    List<Lesson> findAllLessonsByTeacher(String teacher, String name);

    List<Integer> findAllDaysOfWeekByTeacher(String teacher);
}
