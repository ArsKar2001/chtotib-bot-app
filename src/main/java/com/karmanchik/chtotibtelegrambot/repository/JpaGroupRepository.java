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

@Repository
@Transactional(readOnly = true)
public interface JpaGroupRepository extends JpaRepository<Group, Integer> {

    @Query(value = "select s.id from Group s")
    List<Integer> findAllGroupId();

    @Query(value = "select s from Group s where s.groupName like %:year_suffix%")
    List<Group> findAllGroupByYearSuffix(@Param("year_suffix") @NotNull String yearSuffix);

    @Query(
            nativeQuery = true,
            value = "select " +
                    "data.teacher " +
                    "from get_fields_from_json() as data " +
                    "where lower(data.teacher) like '%'||:teacher||'%' " +
                    "group by data.teacher")
    List<String> findAllTeachersByName(@Param("teacher") @NotNull String teacher);

    @Query(nativeQuery = true,
            value = "select " +
                    "data.teacher " +
                    "from get_fields_from_json() as data " +
                    "group by data.teacher")
    List<String> findAllTeachers();

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.groupname as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.dayofweek as dayOfWeek, " +
            "data.lessonnumber as lessonNumber, " +
            "data.weektype as weekType " +
            "from get_fields_from_json() as data " +
            "where data.groupname = :groupName " +
            "order by data.dayofweek, data.lessonnumber")
    List<Lesson> findAllLessonsByGroupName(@Param("groupName") @NotNull String groupName);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.groupname as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.dayofweek as dayOfWeek, " +
            "data.lessonnumber as lessonNumber, " +
            "data.weektype as weekType " +
            "from get_fields_from_json() as data " +
            "where data.groupname = :groupName and data.weektype in (:weekType, 'NONE')" +
            "order by data.dayofweek, data.lessonnumber")
    List<Lesson> findAllLessonsByGroupNameAndWeekType(
            @Param("groupName") @NotNull String groupName,
            @Param("weekType") @NotNull String weekType);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.groupname as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.dayofweek as dayOfWeek, " +
            "data.lessonnumber as lessonNumber, " +
            "data.weektype as weekType " +
            "from get_fields_from_json() as data " +
            "where lower(data.teacher) like '%'||:teacher||'%' and data.weektype in (:weekType, 'NONE')" +
            "order by data.dayofweek, data.lessonnumber")
    List<Lesson> findAllLessonsByTeacherAndWeekType(@Param("teacher") @NotNull String teacher, @Param("weekType") @NotNull String weekType);

    @Query(nativeQuery = true, value = "select " +
            "data.dayofweek " +
            "from get_fields_from_json() as data " +
            "where data.groupname = :groupName " +
            "group by data.dayofweek")
    List<Integer> getListDaysOfWeekByGroupName(@Param("groupName") @NotNull String groupName);

    @Query(nativeQuery = true, value = "select " +
            "data.dayofweek " +
            "from get_fields_from_json() as data " +
            "where data.teacher like '%'||:teacher||'%' " +
            "group by data.dayofweek")
    List<Integer> getListDaysOfWeekByTeacher(@Param("teacher") @NotNull String teacher);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.groupname as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.dayofweek as dayOfWeek, " +
            "data.lessonnumber as lessonNumber, " +
            "data.weektype as weekType " +
            "from get_fields_from_json() as data " +
            "where data.groupname = :groupName " +
            "and data.weektype in (:weekType, 'NONE') " +
            "and data.dayofweek = ''||:dayOfWeek " +
            "order by data.dayofweek, data.lessonnumber")
    List<Lesson> findAllByGroupNameAndDayOfWeek(@Param("groupName") @NotNull String groupName, @Param("dayOfWeek") @NotNull Integer dayOfWeek, @Param("weekType") @NotNull String weekType);

    @Query(nativeQuery = true, value = "SELECT " +
            "data.auditorium as auditorium, " +
            "data.groupname as groupName, " +
            "data.teacher as teacher, " +
            "data.discipline as discipline, " +
            "data.dayofweek as dayOfWeek, " +
            "data.lessonnumber as lessonNumber, " +
            "data.weektype as weekType " +
            "from get_fields_from_json() as data " +
            "where lower(data.teacher) like '%'||:teacher||'%' " +
            "and data.weektype in (:weekType, 'NONE') " +
            "and data.dayofweek = ''||:dayOfWeek " +
            "order by data.dayofweek, data.lessonnumber")
    List<Lesson> findAllByTeacherAndDayOfWeek(@Param("teacher") @NotNull String teacher, @Param("dayOfWeek") @NotNull Integer dayOfWeek, @Param("weekType") @NotNull String weekType);
}
