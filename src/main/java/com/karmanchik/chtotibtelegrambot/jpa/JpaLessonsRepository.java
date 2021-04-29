package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import com.karmanchik.chtotibtelegrambot.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface JpaLessonsRepository extends JpaRepository<Lesson, Integer> {
    Optional<Lesson> getByGroupIdAndDayAndPairNumber(@NotNull Integer groupId,
                                                     @NotNull Integer day,
                                                     @NotNull Integer pairNumber);

    @Query("SELECT l FROM Lesson l " +
            "WHERE :teacher member of l.teachers " +
            "ORDER BY l.pairNumber ASC")
    List<Lesson> findByTeacherOrderByPairNumberAsc(Teacher teacher);

    List<Lesson> findByGroup(Group group);
}
