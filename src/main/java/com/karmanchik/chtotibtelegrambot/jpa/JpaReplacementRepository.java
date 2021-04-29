package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.entity.Replacement;
import com.karmanchik.chtotibtelegrambot.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface JpaReplacementRepository extends JpaRepository<Replacement, Integer> {
    Optional<Replacement> getByGroup(Group group);

    Optional<Replacement> getByGroupAndDate(Group group, @NotNull LocalDate date);

    List<Replacement> findByGroupOrderByDateAscPairNumberAsc(Group group);

    @Query("SELECT r FROM Replacement r " +
            "WHERE :teacher member of r.teachers " +
            "ORDER BY r.date, r.pairNumber")
    List<Replacement> findByTeacherOrderByDateAscPairNumberAsc(@Param("teacher") Teacher teacher);
}
