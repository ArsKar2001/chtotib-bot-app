package com.karmanchik.chtotibtelegrambot.repository;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface JpaGroupRepository extends JpaRepository<Group, Integer> {
    @Query(value = "select g.id, g.groupName, g.timetable from Group g")
    Optional<List<Group>> getListGroup();

    @Query(value = "select s.id from Group s")
    List<Integer> getListGroupId();

    @Query(value = "select s from Group s where s.groupName like %:year_suffix%")
    List<Group> getListGroupNameByYearSuffix(@Param("year_suffix") @NotNull String yearSuffix);

    @Query(
            nativeQuery = true,
            value = "select " +
                    "data.teacher " +
                    "from get_json_fields_from_text() as data " +
                    "where lower(data.teacher) like '%'||:teacher||'%' " +
                    "group by data.teacher")
    List<String> getListTeachersByName(@Param("teacher") @NotNull String teacher);

    @Query(
            nativeQuery = true,
            value = "select " +
                    "data.teacher " +
                    "from get_json_fields_from_text() as data " +
                    "group by data.teacher")
    List<String> findAllTeachers();

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.group_name as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.day_of_week as dayOfWeek, " +
            "data.lesson_number as lessonNumber, " +
            "data.week_type as weekType " +
            "from get_json_fields_from_text() as data " +
            "where data.group_name = :groupName " +
            "order by data.day_of_week, data.lesson_number")
    Optional<List<Lesson>> getListLessonByGroupName(@Param("groupName") @NotNull String groupName);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.group_name as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.day_of_week as dayOfWeek, " +
            "data.lesson_number as lessonNumber, " +
            "data.week_type as weekType " +
            "from get_json_fields_from_text() as data " +
            "where data.group_name = :groupName and data.week_type in (:weekType, 'NONE')" +
            "order by data.day_of_week, data.lesson_number")
    List<Lesson> getListLessonByGroupNameAndWeekType(
            @Param("groupName") @NotNull String groupName,
            @Param("weekType") @NotNull String weekType);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.group_name as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.day_of_week as dayOfWeek, " +
            "data.lesson_number as lessonNumber, " +
            "data.week_type as weekType " +
            "from get_json_fields_from_text() as data " +
            "where lower(data.teacher) like '%'||:teacher||'%' and data.week_type in (:weekType, 'NONE')" +
            "order by data.day_of_week, data.lesson_number")
    List<Lesson> getListLessonByTeacherAndWeekType(
            @Param("teacher") @NotNull String teacher,

            @Param("weekType") @NotNull String weekType);

    @Query(nativeQuery = true, value = "select " +
            "data.day_of_week " +
            "from get_json_fields_from_text() as data " +
            "where data.group_name = :groupName " +
            "group by data.day_of_week")
    List<Integer> getListDaysOfWeekByGroupName(@Param("groupName") @NotNull String groupName);

    @Query(nativeQuery = true, value = "select " +
            "data.day_of_week " +
            "from get_json_fields_from_text() as data " +
            "where data.teacher like '%'||:teacher||'%' " +
            "group by data.day_of_week")
    List<Integer> getListDaysOfWeekByTeacher(@Param("teacher") @NotNull String teacher);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.group_name as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.day_of_week as dayOfWeek, " +
            "data.lesson_number as lessonNumber, " +
            "data.week_type as weekType " +
            "from get_json_fields_from_text() as data " +
            "where data.group_name = :groupName " +
            "and data.week_type in (:weekType, 'NONE') " +
            "and data.day_of_week = ''||:dayOfWeek " +
            "order by data.day_of_week, data.lesson_number")
    List<Lesson> findAllByGroupNameAndDayOfWeek(
            @Param("groupName") @NotNull String groupName,
            @Param("dayOfWeek") @NotNull Integer dayOfWeek,
            @Param("weekType") @NotNull String weekType);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.group_name as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.day_of_week as dayOfWeek, " +
            "data.lesson_number as lessonNumber, " +
            "data.week_type as weekType " +
            "from get_json_fields_from_text() as data " +
            "where lower(data.teacher) like '%'||:teacher||'%' " +
            "and data.week_type in (:weekType, 'NONE') " +
            "and data.day_of_week = ''||:dayOfWeek " +
            "order by data.day_of_week, data.lesson_number")
    List<Lesson> findAllByTeacherAndDayOfWeek(
            @Param("teacher") @NotNull String teacher,
            @Param("dayOfWeek") @NotNull Integer dayOfWeek,
            @Param("weekType") @NotNull String weekType);
}
