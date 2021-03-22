package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.repository.JpaGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private final JpaGroupRepository groupRepository;

    @Override
    public List<String> findAllTeachersByName(String teacher) {
        log.debug("findAllTeachersByName - {}", teacher);
        return groupRepository.findAllTeachersByName(teacher);
    }

    @Override
    public List<Group> findAllGroupNamesByYearSuffix(String academicYearSuffix) {
        log.debug("findAllGroupNamesByYearSuffix - {}", academicYearSuffix);
        return groupRepository.findAllGroupByYearSuffix(academicYearSuffix);
    }

    @Override
    public boolean isGroupId(String message) {
        try {
            log.debug("isGroupId - {}", message);
            List<Integer> groups = groupRepository.findAllGroupId();
            return groups.contains(Integer.parseInt(message));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> findAllTeachers() {
        return groupRepository.findAllTeachers();
    }

    @Override
    public List<Lesson> findAllByGroupNameAndDayOfWeek(String groupName, int nextDayOfWeek, String name) {
        log.debug("findAllByGroupNameAndDayOfWeek - [{}, {}, {}]", groupName, nextDayOfWeek, name);
        return groupRepository.findAllByGroupNameAndDayOfWeek(groupName, nextDayOfWeek, name);
    }

    @Override
    public List<Lesson> findAllByTeacherAndDayOfWeek(String teacher, int nextDayOfWeek, String name) {
        log.debug("findAllByTeacherAndDayOfWeek - [{}, {}, {}]", teacher, nextDayOfWeek, name);
        return groupRepository.findAllByTeacherAndDayOfWeek(teacher, nextDayOfWeek, name);
    }

    @Override
    public List<Integer> findAllDaysOfWeekByGroupName(String groupName) {
        log.debug("getListDaysOfWeekByGroupName - {}", groupName);
        return groupRepository.getListDaysOfWeekByGroupName(groupName);
    }

    @Override
    public List<Lesson> findAllLessonsByTeacher(String teacher, String name) {
        log.debug("findAllLessonsByTeacherAndWeekType - [{}, {}]", teacher, name);
        return groupRepository.findAllLessonsByTeacherAndWeekType(teacher, name);
    }

    @Override
    public List<Integer> findAllDaysOfWeekByTeacher(String teacher) {
        log.debug("getListDaysOfWeekByTeacher - {}", teacher);
        return groupRepository.getListDaysOfWeekByTeacher(teacher);
    }
}
