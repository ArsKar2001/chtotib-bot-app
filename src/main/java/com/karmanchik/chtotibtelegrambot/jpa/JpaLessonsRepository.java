package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Lesson> findByGroupOrderByDayAscPairNumberAsc(Group group);

    @Query("SELECT l FROM Lesson l " +
            "LEFT JOIN l.teachers AS t ON t.id = :tId " +
            "ORDER BY l.day, l.pairNumber ASC")
    List<Lesson> findByTeacherId(@Param("tId") Integer teacherId);
}
